package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CommentDto commentDto) {
        if (!postRepository.existsById(commentDto.getPostId())) {
            throw new DataValidationException("Post with ID " + commentDto.getPostId() + " does not exist.");
        }
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null) {
            throw new DataValidationException("User not found with ID: " + commentDto.getAuthorId());
        }
        LocalDateTime createdAt = LocalDateTime.now();
        Comment comment = commentMapper.toEntity(commentDto, createdAt);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        Comment existingComment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new DataValidationException("Comment with ID " + commentDto.getId() + " does not exist."));

        existingComment.setContent(commentDto.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toDto(updatedComment);
    }

    public List<CommentDto> getAllCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .map(commentMapper::toDto)
                .sorted(Comparator.comparing(CommentDto::getCreatedAt).reversed())
                .toList();
    }

    public void deleteComment(long commentId, long postId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment not found with ID: " + commentId));

        if (comment.getPost().getId() != postId) {
            throw new DataValidationException("Comment with ID " + commentId + " does not belong to post with ID " + postId);
        }

        commentRepository.deleteById(commentId);
    }
}