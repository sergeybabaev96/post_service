package faang.school.postservice.service.post.implementations;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.interfaces.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper = new PostMapperImpl();

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostServiceImpl postService;

    private PostDto inputDto;
    private Post entity;
    private PostDto outputDto;

    @BeforeEach
    void setUp() {
        inputDto = new PostDto();
        inputDto.setId(1L);
        inputDto.setContent("Draft content");

        entity = new Post();
        entity.setId(1L);
        entity.setContent("Draft content");
        //entity.setPublished(false);
        //entity.setDeleted(false);

        outputDto = new PostDto();
        outputDto.setId(1L);
        outputDto.setContent("Draft content");
    }

    @Test
    void testCreatePostDraft_SuccessWithUser() {
        inputDto.setAuthorId(1L);
        inputDto.setProjectId(0L);
        entity.setAuthorId(1L);
        entity.setProjectId(0L);
        outputDto.setAuthorId(1L);
        outputDto.setProjectId(0L);

        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L, "User", "email"));
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        PostDto result = postService.createPostDraft(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Draft content", result.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(userServiceClient, times(1)).getUser(1L);
        verify(projectServiceClient, never()).getProject(anyLong());
    }

    @Test
    void testCreatePostDraft_SuccessWithProject() {
        inputDto.setAuthorId(0L);
        inputDto.setProjectId(1L);
        entity.setAuthorId(0L);
        entity.setProjectId(1L);
        outputDto.setAuthorId(0L);
        outputDto.setProjectId(1L);

        when(projectServiceClient.getProject(1L)).thenReturn(new ProjectDto(1L, "Project"));
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        PostDto result = postService.createPostDraft(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Draft content", result.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(projectServiceClient, times(1)).getProject(1L);
        verify(userServiceClient, never()).getUser(anyLong());
    }

    @Test
    void testCreatePostDraft_NegativeAuthorId() {
        inputDto.setAuthorId(-1L);
        inputDto.setProjectId(0L);

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("ID should not be less than zero!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraft_BothIdsZero() {
        inputDto.setAuthorId(0L);
        inputDto.setProjectId(0L);

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("One author required!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraft_BothIdsNonZero() {
        inputDto.setAuthorId(1L);
        inputDto.setProjectId(1L);

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("The author can be either a user or a project!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraft_UserNotFound() {
        inputDto.setAuthorId(1L);
        inputDto.setProjectId(0L);
        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(0L, "Not Found", "none"));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("User with ID 1 not found!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraft_ProjectNotFound() {
        inputDto.setAuthorId(0L);
        inputDto.setProjectId(1L);
        when(projectServiceClient.getProject(1L)).thenReturn(new ProjectDto(0L, "Not Found"));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("Project with ID 1 not found!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    /****************************************************************************************/
    @Test
    void testPublicPost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        when(postRepository.save(entity)).thenReturn(entity);

        PostDto result = postService.publicPost(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toDto(any(Post.class));
    }

    @Test
    void testPublicPost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostDtoValidationException exception = assertThrows(PostDtoValidationException.class,
                () -> postService.publicPost(inputDto)
        );

        assertEquals("Post with ID 1 does not exist", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toDto(any());
    }

    @Test
    void testPublicPost_PostDeleted() {
        entity.setDeleted(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.publicPost(inputDto)
        );
        assertEquals("The post with ID 1 removed", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toDto(any());
    }

    @Test
    void testPublicPost_AlreadyPublished() {
        entity.setPublished(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.publicPost(inputDto)
        );
        assertEquals("The post with ID 1 has already been published", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toDto(any());
    }

    /****************************************************************************************/
    @Test
    void testUpdatePost_Success() {
        inputDto.setContent("Updated content");
        entity.setContent("Original content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.updatePost(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated content", result.getContent());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdatePost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.updatePost(inputDto)
        );
        assertEquals("Post with ID 1 does not exist", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
    }

    /****************************************************************************************/
    @Test
    void testDeletePost_Success() {
        entity.setContent("Test content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.deletePost(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
        assertTrue(result.isDeleted());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.deletePost(inputDto)
        );
        assertEquals("Post with ID 1 does not exist", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
    }
}