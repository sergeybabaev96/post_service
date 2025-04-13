package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    private static final Long POST_ID = 1L;
    private static final Long COMMENT_ID = 1L;
    private static final Long AUTHOR_ID = 1L;
    private static final Long INVALID_ID = 999L;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private Comment comment;

    @Mock
    private Post post;

    @InjectMocks
    private CommentValidator commentValidator;

    @Nested
    @DisplayName("validateCommentBelongsToPost()")
    class ValidateCommentBelongsToPost {

        @Test
        @DisplayName("Должен пройти успешно, когда комментарий принадлежит посту")
        void givenValidArguments_WhenValidateCommentBelongsToPost_ThenSuccess() {
            when(comment.belongsToPost(POST_ID)).thenReturn(true);

            assertDoesNotThrow(() ->
                    commentValidator.validateCommentBelongsToPost(comment, POST_ID)
            );

            verify(comment).belongsToPost(POST_ID);
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда комментарий не принадлежит посту")
        void givenInvalidArguments_WhenValidateCommentBelongsToPost_ThenThrowDataValidationException() {
            when(comment.belongsToPost(POST_ID)).thenReturn(false);
            when(comment.getId()).thenReturn(COMMENT_ID);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> commentValidator.validateCommentBelongsToPost(comment, POST_ID)
            );

            assertEquals(
                    String.format("Comment with ID %d does not belong to post with ID %d", COMMENT_ID, POST_ID),
                    exception.getMessage()
            );
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда комментарий null")
        void givenNullComment_WhenValidateCommentBelongsToPost_ThenThrowDataValidationException() {
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> commentValidator.validateCommentBelongsToPost(null, POST_ID)
            );

            assertEquals("Comment must not be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validateUserById()")
    class ValidateUserById {

        @Test
        @DisplayName("Проверка существования пользователя")
        void givenValidUserId_WhenValidateUserById_ThenSuccess() {
            when(userServiceClient.getUser(AUTHOR_ID))
                    .thenReturn(new UserDto(AUTHOR_ID, "user", "user@example.com"));

            assertDoesNotThrow(() -> commentValidator.validateUserById(AUTHOR_ID));
            verify(userServiceClient).getUser(AUTHOR_ID);
        }
    }

    @Test
    @DisplayName("Должен выбросить исключение, когда пользователь не существует")
    void givenInvalidUserId_WhenValidateUserById_ThenThrowEntityNotFoundException() {
        when(userServiceClient.getUser(INVALID_ID)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentValidator.validateUserById(INVALID_ID)
        );

        assertEquals(String.format("User with ID %d not found", INVALID_ID), exception.getMessage());
    }

    @Nested
    @DisplayName("validatePostExists()")
    class ValidatePostExists {

        @Test
        @DisplayName("Должен пройти успешно, когда пост существует")
        void givenExistingPostId_WhenValidatePostExists_ThenSuccess() {
            when(postRepository.existsById(POST_ID)).thenReturn(true);

            assertDoesNotThrow(() -> commentValidator.validatePostExists(POST_ID));
            verify(postRepository).existsById(POST_ID);
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда пост не существует")
        void givenNonExistingPostId_WhenValidatePostExists_ThenThrowEntityNotFoundException() {
            when(postRepository.existsById(INVALID_ID)).thenReturn(false);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> commentValidator.validatePostExists(INVALID_ID)
            );

            assertEquals(String.format("Post with ID %d not found", INVALID_ID), exception.getMessage());
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда postId равен null")
        void givenNullPostId_WhenValidatePostExists_ThenThrowIllegalArgumentException() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> commentValidator.validatePostExists(null)
            );

            assertEquals("Post ID must not be null", exception.getMessage());
        }
    }
}