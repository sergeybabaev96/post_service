package faang.school.postservice.service.comment.interfaces;

import faang.school.postservice.dto.comment.CommentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto);

    CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto);

    List<CommentDto> getCommentsByPostId(Long postId);

    void deleteComment(Long postId, Long commentId);
}
