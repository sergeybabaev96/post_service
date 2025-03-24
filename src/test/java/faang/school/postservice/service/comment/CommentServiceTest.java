package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment comment;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto();
        commentDto.setPostId(1L);
        commentDto.setAuthorId(1L);
        commentDto.setContent("Test comment");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUser");

        comment = new Comment();
        comment.setId(1L);
        comment.setPost(new Post());
        comment.setAuthorId(1L);
        comment.setContent("Test comment");
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateCommentWhenPostAndUserExist() {
        when(postRepository.existsById(commentDto.getPostId())).thenReturn(true);
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(userDto);
        when(commentMapper.toEntity(eq(commentDto), any(LocalDateTime.class))).thenReturn(comment); // Исправлено
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(postRepository, times(1)).existsById(commentDto.getPostId());
        verify(userServiceClient, times(1)).getUser(commentDto.getAuthorId());
        verify(commentMapper, times(1)).toEntity(eq(commentDto), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toDto(comment);
    }

    @Test
    void shouldNotCreateCommentWhenPostDoesNotExist() {
        when(postRepository.existsById(commentDto.getPostId())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                commentService.createComment(commentDto));

        assertEquals("Post with ID " + commentDto.getPostId() + " does not exist.", exception.getMessage());
        verify(postRepository, times(1)).existsById(commentDto.getPostId());
        verify(userServiceClient, never()).getUser(anyLong());
        verify(commentMapper, never()).toEntity(any(), any());
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toDto(any());
    }

    @Test
    void shouldNotCreateCommentWhenUserDoesNotExist() {
        when(postRepository.existsById(commentDto.getPostId())).thenReturn(true);
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                commentService.createComment(commentDto));

        assertEquals("User not found with ID: " + commentDto.getAuthorId(), exception.getMessage());
        verify(postRepository, times(1)).existsById(commentDto.getPostId());
        verify(userServiceClient, times(1)).getUser(commentDto.getAuthorId());
        verify(commentMapper, never()).toEntity(any(), any());
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toDto(any());
    }

    @Test
    void shouldUpdateCommentWhenCommentExists() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setContent("Updated content");

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setContent("Old content");

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setContent("Updated content");
        updatedComment.setUpdatedAt(LocalDateTime.now());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(existingComment)).thenReturn(updatedComment);
        when(commentMapper.toDto(updatedComment)).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(commentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        assertEquals("Updated content", result.getContent());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(existingComment);
        verify(commentMapper, times(1)).toDto(updatedComment);
    }

    @Test
    void shouldReturnSortedCommentsWhenFetchingByPostId() {
        long postId = 1L;

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("Comment 1");
        comment1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("Comment 2");
        comment2.setCreatedAt(LocalDateTime.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(1L);
        commentDto1.setContent("Comment 1");
        commentDto1.setCreatedAt(comment1.getCreatedAt());

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(2L);
        commentDto2.setContent("Comment 2");
        commentDto2.setCreatedAt(comment2.getCreatedAt());

        List<CommentDto> expectedCommentDtos = Arrays.asList(commentDto2, commentDto1);

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        List<CommentDto> result = commentService.getAllCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(expectedCommentDtos.size(), result.size());
        assertEquals(expectedCommentDtos, result);

        verify(commentRepository, times(1)).findAllByPostId(postId);
        verify(commentMapper, times(1)).toDto(comment1);
        verify(commentMapper, times(1)).toDto(comment2);
    }

    @Test
    void shouldReturnEmptyListWhenNoCommentsForPost() {

        long postId = 1L;

        when(commentRepository.findAllByPostId(postId)).thenReturn(Collections.emptyList());

        List<CommentDto> result = commentService.getAllCommentsByPostId(postId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentRepository, times(1)).findAllByPostId(postId);
        verify(commentMapper, never()).toDto(any());
    }

    @Test
    void shouldThrowExceptionWhenCommentDoesNotExist() {
        long commentId = 1L;
        long postId = 10L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentService.deleteComment(commentId, postId);
        });

        String expectedMessage = "Comment not found with ID: " + commentId;
        verify(commentRepository, never()).deleteById(anyLong());
        assert exception.getMessage().equals(expectedMessage);
    }

    @Test
    void shouldThrowExceptionWhenCommentDoesNotBelongToPost() {
        long commentId = 1L;
        long postId = 10L;

        Comment comment = new Comment();
        Post post = new Post();
        post.setId(23L);
        comment.setId(commentId);
        comment.setPost(post);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentService.deleteComment(commentId, postId);
        });

        String expectedMessage = "Comment with ID " + commentId + " does not belong to post with ID " + postId;
        verify(commentRepository, never()).deleteById(anyLong());
        assert exception.getMessage().equals(expectedMessage);
    }
}