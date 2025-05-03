package faang.school.postservice.service;

import faang.school.postservice.AbstractIntegrationTest;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaPostPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserJdbcRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

public class PostServiceIT extends AbstractIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserContext userContext;

    @MockBean
    private UserJdbcRepository userJdbcRepository;

    @MockBean
    private KafkaPostPublisher kafkaPostPublisher;

    @Captor
    private ArgumentCaptor<PostEvent> postEventCaptor;

    @Test
    public void testShouldPublishPostAndSendEvent() {
        Post post = Post.builder()
                .content("Test post")
                .authorId(1L)
                .build();
        postRepository.save(post);

        PostEvent expectedEvent = PostEvent.builder()
                .postId(post.getId())
                .authorId(1L)
                .followeeIds(List.of(2L, 3L))
                .build();

        Mockito.when(userJdbcRepository.getFollowersIds(1L)).thenReturn(List.of(2L, 3L));

        PostDto result = postService.publishPost(post.getId());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        Assertions.assertTrue(updatedPost.isPublished());
        Assertions.assertNotNull(updatedPost.getPublishedAt());

        Mockito.verify(kafkaPostPublisher).sendEvent(postEventCaptor.capture());

        PostEvent actualEvent = postEventCaptor.getValue();
        Assertions.assertEquals(expectedEvent.postId(), actualEvent.postId());
        Assertions.assertEquals(expectedEvent.authorId(), actualEvent.authorId());
        Assertions.assertEquals(expectedEvent.followeeIds(), actualEvent.followeeIds());

        Assertions.assertEquals(post.getId(), result.id());
        Assertions.assertTrue(result.published());
    }
}
