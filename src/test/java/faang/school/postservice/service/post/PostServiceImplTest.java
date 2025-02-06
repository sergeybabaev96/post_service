package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.properties.post.PostProperties;
import faang.school.postservice.properties.user.UserBanProperties;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserBanProperties userBanProperties;

    @Mock
    private PostProperties postProperties;

    @Mock
    private RedisPublisher redisPublisher;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void testBanUsers() {
        when(postRepository.findByVerified(eq(false))).thenReturn(List.of(getPost()));
        when(postProperties.getMaxUnverified()).thenReturn(0);
        when(userBanProperties.getChannel()).thenReturn("channel");
        doNothing().when(redisPublisher).publish(eq("channel"), eq(String.valueOf(1)));

        postService.banUsersWithManyUnverifiedPosts();

        verify(postRepository).findByVerified(false);
    }

    private Post getPost() {
        return Post.builder()
                .id(1L)
                .authorId(1L)
                .build();
    }

}