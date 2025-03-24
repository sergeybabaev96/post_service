package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeValidator likeValidator;

    private Long postId;
    private Long commentId;
    private Long userId;
    private Post post;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        post = new Post();
        comment = new Comment();

        postId = 1L;
        commentId = 2L;
        userId = 3L;

        post.setId(postId);
        comment.setId(commentId);
        comment.setPost(post);
        post.setComments(List.of(comment));
    }

    @Test
    @DisplayName("validatePostLikeConditions: валидные postId и userId, не выбрасывает исключение")
    public void givenValidPostAndUserWhenValidatePostLikeConditionsThenSuccess() {
        Mockito.when(postService.getPostEntity(postId)).thenReturn(post);

        Assertions.assertDoesNotThrow(() -> likeValidator.validatePostLikeConditions(postId, userId));

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(postId, userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    @DisplayName("validatePostLikeConditions: пользователь уже ставил лайк на пост, выбрасывается DataValidationException")
    public void givenUserAlreadyLikedPostWhenValidatePostLikeConditionsThenThrowDataValidationException() {
        Mockito.when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.of(new Like()));

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validatePostLikeConditions(postId, userId));
        Assertions.assertEquals(String.format("User with ID %d has already liked post with ID %d", userId, postId),
                exception.getMessage());

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("validatePostLikeConditions: пользователь уже ставил лайк на комментарий, выбрасывается DataValidationException")
    public void givenUserAlreadyLikedCommentWhenValidatePostLikeConditionsThenThrowDataValidationException() {
        Mockito.when(postService.getPostEntity(postId)).thenReturn(post);
        Mockito.when(likeRepository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.of(new Like()));

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validatePostLikeConditions(postId, userId));
        Assertions.assertEquals(String.format("User with ID %d has already liked comment with ID %d", userId, commentId),
                exception.getMessage());

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(postId, userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    @DisplayName("validatePostUnlikeConditions: валидные postId и userId, не выбрасывает исключение")
    public void givenValidPostAndUserWhenValidatePostUnlikeConditionsThenSuccess() {
        Mockito.when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.of(new Like()));

        Assertions.assertDoesNotThrow(() -> likeValidator.validatePostUnlikeConditions(postId, userId));

        Mockito.verify(postService, Mockito.times(1)).getPostEntity(postId);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("validatePostUnlikeConditions: пользователь не ставил лайк на пост, выбрасывается DataValidationException")
    public void givenPostWithoutLikeWhenValidatePostUnlikeConditionsThenThrowDataValidationException() {
        Mockito.when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validatePostUnlikeConditions(postId, userId));
        Assertions.assertEquals(String.format("User with ID %d has not liked post with ID %d", userId, postId),
                exception.getMessage());

        Mockito.verify(postService, Mockito.times(1)).getPostEntity(postId);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("validateCommentLikeConditions: валидные данные, не выбрасывается исключение")
    public void givenValidCommentAndUserWhenValidateCommentLikeConditionsThenSuccess() {
        Mockito.when(commentService.getComment(commentId)).thenReturn(comment);

        Assertions.assertDoesNotThrow(() ->
                likeValidator.validateCommentLikeConditions(commentId, userId));

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentIdAndUserId(commentId, userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("validateCommentLikeConditions: пользователь уже ставил лайк на комментарий, выбрасывается DataValidationException")
    public void givenUserAlreadyLikedCommentWhenValidateCommentLikeConditionsThenThrowDataValidationException() {
        Mockito.when(likeRepository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.of(new Like()));

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validateCommentLikeConditions(commentId, userId));
        Assertions.assertEquals(String.format("User with ID %d has already liked comment with ID %d", userId, commentId),
                exception.getMessage());

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    @DisplayName("validateCommentLikeConditions: пользователь уже ставил лайк на пост, выбрасывается DataValidationException")
    public void givenUserAlreadyLikedPostOfCommentWhenValidateCommentLikeConditionsThenThrowDataValidationException() {
        Mockito.when(commentService.getComment(commentId)).thenReturn(comment);
        Mockito.when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.of(new Like()));

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validateCommentLikeConditions(commentId, userId));
        Assertions.assertEquals(String.format("User with ID %d has already liked post with ID %d", userId, postId),
                exception.getMessage());

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentIdAndUserId(commentId, userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("validateCommentUnlikeConditions: валидные данные, не выбрасывается исключение")
    public void givenValidCommentAndUserWithLikeWhenValidateCommentUnlikeConditionsThenSuccess() {
        Mockito.when(likeRepository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.of(new Like()));

        Assertions.assertDoesNotThrow(() ->
                likeValidator.validateCommentUnlikeConditions(commentId, userId));

        Mockito.verify(commentService, Mockito.times(1)).getComment(commentId);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    @DisplayName("validateCommentUnlikeConditions: пользователь не ставил лайк на комментарий")
    public void givenCommentWithoutUserLikeWhenValidateCommentUnlikeConditionsThenThrowException() {
        Mockito.when(likeRepository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validateCommentUnlikeConditions(commentId, userId));
        Assertions.assertEquals(String.format("User with ID %d has not liked comment with ID %d", userId, commentId),
                exception.getMessage());

        Mockito.verify(commentService, Mockito.times(1)).getComment(commentId);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(userId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentIdAndUserId(commentId, userId);
    }
}
