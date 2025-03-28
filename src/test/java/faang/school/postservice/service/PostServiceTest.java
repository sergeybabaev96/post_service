package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
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

   /* @Test
    void createDraft2() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 3, 27, 12, 0, 0);
        doNothing().when(postValidator).validatePostDto(postDto);
       when(postRepository.save(post)).thenReturn(post);

        post.setCreatedAt(fixedTime);


        PostDto createdDraft = postService.createDraft(postDto);


        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        LocalDateTime capturedTime = postCaptor.getValue().getCreatedAt();
        assertThat(capturedTime).isCloseTo(LocalDateTime.now(), within(500, ChronoUnit.MILLIS));

        assertEquals(postDto.content(), createdDraft.content());
        assertEquals(postDto.id(),createdDraft.id());
        assertNull(createdDraft.projectId());
        assertFalse(createdDraft.published());
       // assertThat(postDto.CreatedAt()).isCloseTo(post.CreatedAt(), within(500, ChronoUnit.MILLIS));


    }*/

    @Test
    void createDraft() {
        // Фиксированное время
        LocalDateTime fixedTime = LocalDateTime.of(2025, 3, 27, 12, 0, 0);

        // Мокаем валидацию, если нужно
        doNothing().when(postValidator).validatePostDto(postDto);


        // Создаем объект Post и устанавливаем фиксированное время
        //Post post = new Post();
        post.setCreatedAt(fixedTime);

        // Мокаем маппинг DTO → Entity
        // when(postMapper.toEntity(postDto)).thenReturn(post);

        // Мокаем save() и заменяем createdAt на фиксированное время
        doAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setCreatedAt(fixedTime);
            return savedPost;
        }).when(postRepository).save(post);

        // Вызываем тестируемый метод
        PostDto createdDraft = postService.createDraft(postDto);

        // Проверяем, что `save()` вызван с нужными параметрами
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        // Извлекаем зафиксированное время
        LocalDateTime capturedTime = postCaptor.getValue().getCreatedAt();

        // Проверяем, что установлено именно фиксированное время
        assertEquals(fixedTime, capturedTime);

        // Проверяем соответствие данных
        assertEquals(postDto.content(), createdDraft.content());
        assertEquals(postDto.id(), createdDraft.id());
        assertNull(createdDraft.projectId());
        assertFalse(createdDraft.published());
        assertEquals(postDto.createdAt(), createdDraft.createdAt());
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