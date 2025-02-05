package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private static final long ID = 1L;
    private static final long AUTHOR_ID = 2L;
    private static final long PROJECT_ID = 10L;
    private static final String TEST_CONTENT = "Тестовая запись";
    private static final String UPDATE_CONTENT = "Обновленная запись";
    private static final String POST_NOT_FOUND = "Пост не найден";
    private static final String EXCEPTION_MESSAGE = "Пост уже опубликован и не может быть опубликован повторно";

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private PostService postService;

    private Post post;
    private Post draftPost;
    private Post publishedPost;
    private Post deletedPost;
    private PostResponseDto postResponseDto;
    private PostResponseDto draftDto;
    private PostResponseDto publishedDto;
    private PostResponseDto expectedResponse;
    private CreatePostDto createPostDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(ID)
                .authorId(AUTHOR_ID)
                .projectId(PROJECT_ID)
                .content(TEST_CONTENT)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        postResponseDto = PostResponseDto.builder()
                .id(ID)
                .authorId(AUTHOR_ID)
                .projectId(PROJECT_ID)
                .content(TEST_CONTENT)
                .published(false)
                .deleted(false)
                .build();

        draftPost = Post.builder()
                .id(ID)
                .authorId(AUTHOR_ID)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        publishedPost = Post.builder()
                .id(ID)
                .authorId(AUTHOR_ID)
                .published(true)
                .deleted(false)
                .build();

        draftDto = PostResponseDto.builder()
                .id(ID)
                .authorId(AUTHOR_ID)
                .published(false)
                .deleted(false)
                .build();

        deletedPost = Post.builder()
                .id(ID)
                .projectId(PROJECT_ID)
                .published(false)
                .deleted(true)
                .createdAt(LocalDateTime.now())
                .build();

        publishedDto = PostResponseDto.builder()
                .id(ID)
                .projectId(PROJECT_ID)
                .published(true)
                .deleted(false)
                .build();

        createPostDto = CreatePostDto.builder()
                .authorId(AUTHOR_ID)
                .content(TEST_CONTENT)
                .published(false)
                .build();

        expectedResponse = PostResponseDto.builder()
                .id(ID)
                .published(true)
                .build();
    }

    @Test
    void createPostSuccessfullyTest() {

        when(postMapper.toEntity(createPostDto)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postResponseDto);

        PostResponseDto response = postService.create(createPostDto);

        assertNotNull(response);
        assertEquals(TEST_CONTENT, response.getContent());
        assertEquals(AUTHOR_ID, response.getAuthorId());
        assertFalse(response.getPublished());

        verify(postValidator).validateDraftPost(createPostDto);
        verify(postRepository).save(any(Post.class));
        verify(postMapper).toDto(post);
    }

    @Test
    void getPostSuccessfullyTest() {
        when(postRepository.findById(ID)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postResponseDto);

        PostResponseDto response = postService.getPost(ID);

        assertNotNull(response);
        assertEquals(ID, response.getId());
        assertEquals(TEST_CONTENT, response.getContent());

        verify(postRepository).findById(ID);
        verify(postMapper).toDto(post);
    }

    @Test
    void updatePostSuccessfullyTest() {

        Post post = new Post();
        post.setContent(TEST_CONTENT);

        PostResponseDto postResponseDto = PostResponseDto.builder()
                .content(UPDATE_CONTENT)
                .build();

        UpdatePostDto updatePostDto = new UpdatePostDto(UPDATE_CONTENT);

        when(postRepository.findById(ID)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(any(Post.class))).thenReturn(postResponseDto);

        PostResponseDto response = postService.update(ID, updatePostDto);

        assertEquals(UPDATE_CONTENT, response.getContent());
    }

    @Test
    void publishSuccessfullyTest() {

        when(postRepository.findById(ID)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedResponse);

        PostResponseDto response = postService.publish(ID);

        assertNotNull(response);
        assertTrue(response.getPublished());

        verify(postValidator).validateNotPublished(post);
        verify(postValidator).validateNotDeleted(post);
        verify(postValidator).validatePostAuthorExist(post);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    void deletePostSuccessfullyTest() {
        long postId = ID;
        Post post = new Post();
        post.setId(postId);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postService.delete(postId);

        assertTrue(post.isDeleted());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void getDraftPostsByAuthorIdTest() {

        List<Post> posts = Arrays.asList(draftPost, publishedPost);
        when(postRepository.findByAuthorId(ID)).thenReturn(posts);

        when(postMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(draftDto));

        List<PostResponseDto> result = postService.getDraftPostsByAuthorId(ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getPublished());
        assertEquals(ID, result.get(0).getId());

        verify(postRepository).findByAuthorId(ID);
        verify(postMapper).toDtoList(anyList());
    }

    @Test
    void getDraftPostsByProjectIdTest() {

        List<Post> posts = Arrays.asList(draftPost, deletedPost);
        when(postRepository.findByProjectId(PROJECT_ID)).thenReturn(posts);

        when(postMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(draftDto));

        List<PostResponseDto> result = postService.getDraftPostsByProjectId(PROJECT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getPublished());
        assertEquals(ID, result.get(0).getId());

        verify(postRepository).findByProjectId(PROJECT_ID);
        verify(postMapper).toDtoList(anyList());
    }

    @Test
    void exceptionWhenPostNotFoundTest() {
        when(postRepository.findById(ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class, () -> postService.getPost(ID));

        assertEquals(POST_NOT_FOUND, exception.getMessage());
        verify(postRepository).findById(ID);
        verifyNoInteractions(postMapper);
    }

    @Test
    void exceptionWhenPostIsDeletedTest() {
        post.setDeleted(true);
        when(postRepository.findById(ID)).thenReturn(Optional.of(post));

        Exception exception = assertThrows(DataNotFoundException.class, () -> postService.getPost(ID));

        assertEquals(POST_NOT_FOUND, exception.getMessage());
        verify(postRepository).findById(ID);
        verifyNoInteractions(postMapper);
    }

    @Test
    void exceptionWhenAlreadyPublishedPostTest() {

        when(postRepository.findById(ID)).thenReturn(Optional.of(post));

        doThrow(new DataValidationException(EXCEPTION_MESSAGE))
                .when(postValidator).validateNotPublished(post);

        Exception exception = assertThrows(DataValidationException.class, () -> postService.publish(ID));

        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());

        verify(postValidator).validateNotPublished(post);

        verify(postValidator, never()).validateNotDeleted(post);
        verify(postValidator, never()).validatePostAuthorExist(post);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void onlyPublishedByProjectIdTest() {

        List<Post> posts = Collections.singletonList(publishedPost);

        when(postRepository.findByProjectId(PROJECT_ID)).thenReturn(posts);

        List<PostResponseDto> expectedDto = Collections.singletonList(publishedDto);
        when(postMapper.toDtoList(anyList())).thenReturn(expectedDto);

        List<PostResponseDto> result = postService.getPublishedPostsByProjectId(PROJECT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPublished());
        assertEquals(ID, result.get(0).getId());

        verify(postRepository).findByProjectId(PROJECT_ID);
        verify(postMapper).toDtoList(anyList());
    }

    @Test
    void noDeletedByProjectIdTest() {

        List<Post> posts = Collections.singletonList(deletedPost);

        when(postRepository.findByProjectId(PROJECT_ID)).thenReturn(posts);
        when(postMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        List<PostResponseDto> result = postService.getPublishedPostsByProjectId(PROJECT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository).findByProjectId(PROJECT_ID);
        verify(postMapper).toDtoList(anyList());
    }

    @Test
    void onlyPublishedByAuthorIdTest() {

        List<Post> posts = Collections.singletonList(publishedPost);

        when(postRepository.findByAuthorId(ID)).thenReturn(posts);
        List<PostResponseDto> expectedDtos = Collections.singletonList(publishedDto);
        when(postMapper.toDtoList(anyList())).thenReturn(expectedDtos);

        List<PostResponseDto> result = postService.getPublishedPostsByAuthorId(ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPublished());
        assertEquals(ID, result.get(0).getId());

        verify(postRepository).findByAuthorId(ID);
        verify(postMapper).toDtoList(anyList());
    }

    @Test
    void noDraftsByAuthorIdTest() {

        List<Post> posts = Collections.singletonList(draftPost);

        when(postRepository.findByAuthorId(ID)).thenReturn(posts);
        when(postMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        List<PostResponseDto> result = postService.getPublishedPostsByAuthorId(ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository).findByAuthorId(ID);
        verify(postMapper).toDtoList(anyList());
    }
}