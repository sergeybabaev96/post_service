package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
private static final long EXISTENT_POST_ID = 1L;
private static final  long NON_EXISTENT_POST_ID = 2L;
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 4, 1, 12, 0, 0);;

    private PostDto postDto;
    private PostDto publishedpostDto;
    private Post post;
    private Post publishedPost;


    @Mock
    private Clock fixedClock;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder().content("Test Content").authorId(1L).published(false).build();
        post = Post.builder().content("Test Content").authorId(1L).published(false).build();
        publishedpostDto = PostDto.builder().id(1L).content("Test Content").authorId(1L).published(true).build();
        publishedPost = Post.builder().id(1L).content("Test Content").authorId(1L).published(true).build();
    }

    @Test
    void createDraft() {
        //Arrange

        PostDto postDtoWithCreatedAt = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        Post postWithCreatedAt = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .createdAt(FIXED_TIME)
                .published(false)
                .build();
        when(fixedClock.instant()).thenReturn(FIXED_TIME.atZone(ZoneId.systemDefault()).toInstant());
        when(fixedClock.getZone()).thenReturn(ZoneId.systemDefault());
        doNothing().when(postValidator).validatePostDto(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDtoWithCreatedAt);

        //Act
        PostDto createdDraft = postService.createDraft(postDto);

        //Assert
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(postWithCreatedAt, postCaptor.getValue());

        assertEquals(postDto.content(), createdDraft.content());
        assertEquals(postDto.id(), createdDraft.id());
        assertNull(createdDraft.projectId());
        assertFalse(createdDraft.published());
        assertEquals(postDtoWithCreatedAt.createdAt(), createdDraft.createdAt());
    }



    @Test
    void testGetPostShouldThrowExceptionWhenPostDoesNotExist() {
        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());
        assertThrows(PostValidationException.class, () -> postService.getPost(NON_EXISTENT_POST_ID));
    }

    @Test
    void testGetPostShouldReturnPostDtoWhenPostExists() {
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto actualPostDto = postService.getPost(EXISTENT_POST_ID);

        assertEquals(postDto, actualPostDto);
    }

    @Test
    void testPublishPostShouldThrowExceptionWhenPostDoesNotExist() {
        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());
        assertThrows(PostValidationException.class,() ->
                postService.publishPost(NON_EXISTENT_POST_ID));
    }

    @Test
    void testPublishPostWhenPostDoesIsUnpublished() {
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(post));
        when(postMapper.toDto(publishedPost)).thenReturn(publishedpostDto);

        PostDto actualPostDto = postService.publishPost(EXISTENT_POST_ID);
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(publishedPost,postCaptor.getValue());
    }

    @Test
    void testPublishPostWhenPostAlreadyExist() {
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(publishedPost));

        assertThrows(PostValidationException.class, () ->
                postService.publishPost(EXISTENT_POST_ID));
    }

    @Test
    void testUpdatePostShouldThrowExceptionWhenPostDoesNotExist() {
        String updatedContent = "Updated content";
        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostValidationException.class,() ->
                postService.updatePost(NON_EXISTENT_POST_ID, updatedContent));
    }

    @Test
    void testUpdatePostShouldUpdateContentWhenPostExists() {
        String updatedContent = "Updated content";
        PostDto updatedPostDto = PostDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .published(true)
                .updatedAt(FIXED_TIME)
                .build();
        Post updatedPost = Post.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .published(true)
                .updatedAt(FIXED_TIME)
                .build();
        when(fixedClock.instant()).thenReturn(FIXED_TIME.atZone(ZoneId.systemDefault()).toInstant());
        when(fixedClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(publishedPost));
      //  when(postRepository.save(updatedPost)).thenReturn(updatedPost);
        //when(postMapper.toDto(updatedPost)).thenReturn(updatedPostDto);
        when(postMapper.toDto(any(Post.class))).thenAnswer(invocation -> {
            Post postArg = invocation.getArgument(0);
            return PostDto.builder()
                    .id(postArg.getId())
                    .content(postArg.getContent())
                    .authorId(postArg.getAuthorId())
                    .published(postArg.isPublished())
                    .updatedAt(postArg.getUpdatedAt())
                    .build();
        });

        PostDto actualUpdatedPostDto = postService.updatePost(EXISTENT_POST_ID, updatedContent);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();
        assertEquals(updatedContent, capturedPost.getContent());
        assertEquals(FIXED_TIME, capturedPost.getUpdatedAt());
        assertEquals(EXISTENT_POST_ID, capturedPost.getId());

        assertEquals(updatedContent, actualUpdatedPostDto.content());
        assertEquals(FIXED_TIME, actualUpdatedPostDto.updatedAt());


      /*  assertEquals(updatedPost,postCaptor.getValue());

        assertEquals(updatedPostDto, actualUpdatedPostDto);*/

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