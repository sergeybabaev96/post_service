package faang.school.postservice.service.comment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.сomment.CommentDto;
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

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Post post;
    private UserDto userDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto();
        commentDto.setPostId(1L);
        commentDto.setAuthorId(1L);
        commentDto.setContent("Test comment");

        post = new Post();
        post.setId(1L);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("Test User");

        comment = new Comment();
        comment.setId(1L);
        comment.setPost(post);
        comment.setAuthorId(1L);
        comment.setContent("Test comment");
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createComment_ShouldReturnCommentDto_WhenPostAndUserExist() {
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(userDto);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(postRepository, times(1)).findById(commentDto.getPostId());
        verify(userServiceClient, times(1)).getUser(commentDto.getAuthorId());
        verify(commentMapper, times(1)).toEntity(commentDto);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toDto(comment);
    }

    @Test
    void createComment_ShouldThrowDataValidationException_WhenPostNotFound() {
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentService.createComment(commentDto);
        });

        assertEquals("Post not found with ID: " + commentDto.getPostId(), exception.getMessage());
        verify(postRepository, times(1)).findById(commentDto.getPostId());
        verify(userServiceClient, never()).getUser(anyLong());
        verify(commentMapper, never()).toEntity(any());
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toDto(any());
    }

    @Test
    void createComment_ShouldThrowDataValidationException_WhenUserNotFound() {
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentService.createComment(commentDto);
        });

        assertEquals("User not found with ID: " + commentDto.getAuthorId(), exception.getMessage());
        verify(postRepository, times(1)).findById(commentDto.getPostId());
        verify(userServiceClient, times(1)).getUser(commentDto.getAuthorId());
        verify(commentMapper, never()).toEntity(any());
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toDto(any());
    }
}
