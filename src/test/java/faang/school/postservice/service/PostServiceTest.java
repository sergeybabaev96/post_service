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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    PostDto postDto;
    Post post;
    @Mock
    private Clock fixedClock;
    private LocalDateTime fixedTime;
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
    }

    @Test
    void createDraft() {
        //Arrange
        fixedTime = LocalDateTime.of(2025, 4, 1, 12, 0, 0);
        PostDto postDtoWithCreatedAt = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(fixedTime)
                .build();
        Post postWithCreatedAt = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .createdAt(fixedTime)
                .published(false)
                .build();
        when(fixedClock.instant()).thenReturn(fixedTime.atZone(ZoneId.systemDefault()).toInstant());
        when(fixedClock.getZone()).thenReturn(ZoneId.systemDefault());
        doNothing().when(postValidator).validatePostDto(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(postWithCreatedAt);
        when(postMapper.toDto(postWithCreatedAt)).thenReturn(postDtoWithCreatedAt);

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
        long nonExistentPostId = 2L;
        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());
        assertThrows(PostValidationException.class, () -> postService.getPost(nonExistentPostId));
    }

    @Test
    void testGetPostShouldReturnPostDtoWhenPostExists() {
        long existentPostId = 1L;
        when(postRepository.findById(existentPostId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto actualPostDto = postService.getPost(existentPostId);

        assertEquals(postDto, actualPostDto);
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