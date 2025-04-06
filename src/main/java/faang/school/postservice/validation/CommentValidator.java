package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Валидатор для проверки корректности данных комментариев.
 * Содержит методы валидации перед выполнением операций с комментариями.
 *
 * <p>Основные методы валидации:</p>
 * <ul>
 *   <li>{@link #validateCommentBelongsToPost(Comment, Long, Long)} - Проверка принадлежности комментария посту</li>
 *   <li>{@link #validateUserById(Long)} - Проверка существования пользователя</li>
 * </ul>
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;

    /**
     * Проверяет принадлежность комментария указанному посту.
     *
     * @param comment   проверяемый комментарий
     * @param postId    ожидаемый ID поста
     * @param commentId ID комментария для сообщения об ошибке
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     */
    public void validateCommentBelongsToPost(Comment comment, Long postId, Long commentId) {
        if (comment == null || !comment.belongsToPost(postId)) {
            log.error("Comment with ID {} doesn't belong to post with ID {}", commentId, postId);
            throw new DataValidationException("Comment with ID " + commentId
                    + " doesn't belong to post with ID " + postId);
        }
    }

    /**
     * Проверяет, принадлежит ли указанный комментарий заданному посту.
     *
     * @param comment проверяемый комментарий
     * @param postId  ID поста для проверки принадлежности
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     * @see CommentService#getCommentById(Long)
     * @see DataValidationException
     */
    public void validateCommentBelongsToPost(Comment comment, Long postId) {
        if (!comment.getPost().getId().equals(postId)) {
            log.error("Comment with ID {} does not belong to post with ID {}", comment.getId(), postId);
            throw new DataValidationException(
                    String.format("Comment with ID %d does not belong to post with ID %d",
                            comment.getId(), postId)
            );
        }
    }

    /**
     * Проверяет существование пользователя в системе.
     *
     * @param authorId ID пользователя для проверки
     * @throws EntityNotFoundException если пользователь с указанным ID не найден
     */
    public void validateUserById(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        if (userDto == null) {
            log.error("User with ID {} not found", authorId);
            throw new EntityNotFoundException("User with ID " + authorId + " not found");
        }
    }

    /**
     * Проверяет существование поста с указанным идентификатором.
     *
     * @param postId идентификатор поста для проверки
     * @throws EntityNotFoundException  если пост с указанным ID не найден
     * @throws IllegalArgumentException если переданный postId равен null
     */
    public void validatePostExists(Long postId) {
        if (postId == null) {
            log.error("Post ID must not be null");
            throw new IllegalArgumentException("Post ID must not be null");
        }
        if (!postRepository.existsById(postId)) {
            log.error("Post with ID {} not found", postId);
            throw new EntityNotFoundException("Post with ID " + postId + " not found");
        }
    }
}