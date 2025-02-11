package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeValidator {

    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void validateUserExists(long userId) {

        try {
            userServiceClient.getUser(userId);
        } catch (FeignException ex) {
            log.error("User not exist with userID = {}", userId, ex);
            throw new EntityNotFoundException("User not exist!");
        }
    }

    public Post validateAndGetPost(long postId) {

        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with ID = {} does not exist", postId);
            return new EntityNotFoundException("Post does not exist!");
        });
    }


    public void validatePostLiked(long postId, long userId) {

        if (isPostLikedByUser(postId, userId)) {
            log.error("User id = {} cannot like with post id = {}, already liked", userId, postId);
            throw new DataValidationException("You already liked this post!");
        }
    }

    public Comment validateAndGetComment(long commentId) {

        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with ID = {} does not exist", commentId);
            return new EntityNotFoundException("Comment does not exist!");
        });
    }


    public void validateCommentLiked(long commentId, long userId) {

        if (isCommentLikedByUser(commentId, userId)) {
            log.error("UserId = {} cannot like with commentId = {}, already liked", userId, commentId);
            throw new DataValidationException("You already liked this comment!");
        }
    }

    public void validateUserId(Long userId) {

        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be more 0!");
        }
    }

    private boolean isPostLikedByUser(long postId, long userId) {

        log.debug("Searching existent like with post id = {}, user id = {}", postId, userId);
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private boolean isCommentLikedByUser(long commentId, long userId) {

        log.debug("Searching existent like with comment id = {}, user id = {}", commentId, userId);
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }
}