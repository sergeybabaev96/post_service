package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.creatpost.CreatePostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CreatePostValidator createPostValidator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostService postService;

    private final String TEST_CONTENT = "Test post content";

    private final Post inputPost = new Post();
    private final Post savedPost = new Post();
    private final UserDto userDto = new UserDto();

    @BeforeEach
    void setUp() {
        inputPost.setAuthorId(1L);
        inputPost.setContent(TEST_CONTENT);

        savedPost.setId(100L);
        savedPost.setAuthorId(1L);
        savedPost.setContent(TEST_CONTENT);

        userDto.setId(1L);
    }

    @Test
    void createPost_shouldValidateAndSavePost() {
        when(postRepository.save(inputPost)).thenReturn(savedPost);

        Post result = postService.createPost(inputPost);

        verify(createPostValidator).validateIsPostCreator(inputPost);
        verify(postRepository).save(inputPost);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(TEST_CONTENT, result.getContent());
    }

    @Test
    void createPost_shouldValidateAndSavePostWithProjectId() {
        when(postRepository.save(inputPost)).thenReturn(savedPost);

        Post result = postService.createPost(inputPost);

        verify(createPostValidator).validateIsPostCreator(inputPost);
        verify(postRepository).save(inputPost);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(TEST_CONTENT, result.getContent());
    }

    @Test
    void createPost_shouldThrowExceptionValidationFails() {
        doThrow(new EntityNotFoundException(""))
                .when(createPostValidator).validateIsPostCreator(inputPost);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.createPost(inputPost)
        );

        assertEquals("", exception.getMessage());
        verify(postRepository,never()).save(inputPost);
    }

    @Test
    void createPost_validation_whenUserExists_shouldPass() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        assertDoesNotThrow(() -> createPostValidator.validateIsPostCreator(inputPost));
        verify(userServiceClient).getUser(1L);
        verify(projectServiceClient, never()).getProject(any());
    }

}
