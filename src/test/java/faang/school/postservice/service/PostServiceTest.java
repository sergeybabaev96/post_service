package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    PostDto postDto;
    Post post;
    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        postDto = new PostDto(null, "test text", 1L, null, false, null);
        post = Post.builder().content("test text").authorId(1L).published(false).build();
    }

    @Test
    void createDraft() {
        doNothing().when(postValidator).validatePostDto(postDto);

        verify(postRepository, times(1)).save(post);

    }

    @Test
    void getPost() {
    }

    @Test
    void publishPost() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void softDeletePost() {
    }

    @Test
    void getAllDraftsByAuthorId() {
    }

    @Test
    void getAllDraftsByProjectId() {
    }

    @Test
    void getAllPostsByAuthorId() {
    }

    @Test
    void getAllPostsByProjectId() {
    }
}