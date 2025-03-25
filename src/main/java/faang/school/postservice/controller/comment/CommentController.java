package faang.school.postservice.controller.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable long postId, @Valid @RequestBody CommentDto commentDto) {
        commentDto.setPostId(postId);
        CommentDto createdComment = commentService.createComment(commentDto);
        log.info("Received a request to create a comment for post with ID: {}, comment data: {}", postId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllCommentsByPostId(@PathVariable long postId) {
        List<CommentDto> comments = commentService.getAllCommentsByPostId(postId);
        log.info("Received request to fetch comments for post ID: {}", postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable long commentId, @Valid @RequestBody CommentDto commentDto) {
        commentDto.setId(commentId);
        CommentDto updatedComment = commentService.updateComment(commentDto);
        log.info("Received request to update comment with ID: {}", commentId);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        log.info("Received request to delete comment with ID: {}", commentId);
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}