package faang.school.postservice.util.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exceptions.PostAlreadyPublishedException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private AlbumRepository albumRepository;
    @InjectMocks
    private PostService postService;

    @Test
    public void testPositivePublish() {
        Post post = Post.builder()
                .id(1L)
                .published(false)
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        PostDto postDto = postService.publish(post.getId());
        verify(postRepository, times(1)).save(post);

        assertEquals(post.getId(), postDto.id());
        assertTrue(postDto.published());
    }

    @Test
    public void testNegativePublishPostIdIsNull() {
        assertThrows(RuntimeException.class, () -> postService.publish(null));
    }

    @Test
    public void testNegativePublishIsNotPublished() {
        Post post = Post.builder()
                .id(1L)
                .published(true)
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        assertThrows(PostAlreadyPublishedException.class, () -> postService.publish(post.getId()));
    }

    @Test
    public void update() {
        Post post = Post.builder()
                .id(1L)
                .published(true)
                .build();
        PostDto postDto = PostDto.builder()
                .content("content")
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        PostDto postDto1 = postService.update(postDto, 1L);
        verify(postRepository, times(1)).save(post);
        assertEquals(postDto1.content(), postDto.content());
    }

    @Test
    public void testNegativeUpdatePostDtoIsNull() {
        assertThrows(NullPointerException.class, () -> postService.update(null, 1L));
    }

    @Test
    public void testNegativeUpdateContentIsBlank() {
        assertThrows(NullPointerException.class, () -> postService.update(PostDto.builder()
                .content("")
                .build(), 1L));
    }

    @Test
    public void testNegativeUpdateContentIsNull() {
        assertThrows(NullPointerException.class, () -> postService.update(PostDto.builder()
                .content(null)
                .build(), 1L));
    }

    @Test
    public void testPositiveDelete() {
        Post post = Post.builder()
                .id(1L)
                .published(true)
                .deleted(false)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.deleteById(post.getId());
        verify(postRepository, times(1)).save(post);
        assertTrue(post.isDeleted());
        assertFalse(post.isPublished());
    }
    @Test
    public void testNegativeDeletePostIdIsNull() {
        assertThrows(NullPointerException.class, () -> postService.deleteById(null));
    }
    @Test
    public void testPositiveGetPost(){
        Post post = Post.builder()
                .id(1L)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        PostDto dto = postService.getPost(post.getId());
        assertEquals(post.getId(), dto.id());
    }
    @Test
    public void testNegativeGetPostIdIsNull() {
        assertThrows(NullPointerException.class, () -> postService.getPost(null));
    }
}
