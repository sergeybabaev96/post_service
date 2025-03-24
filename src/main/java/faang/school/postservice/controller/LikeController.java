package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeViewDto;
import faang.school.postservice.service.LikeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки операций с лайками постов и комментариев.
 * Предоставляет API для добавления и удаления лайков.
 *
 * @author gulnaz21
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class LikeController {
    private final LikeService likeService;
    private final UserContext userContext;

    /**
     * Добавляет лайк к посту.
     *
     * @param postId Идентификатор поста, к которому добавляется лайк. Не может быть null и должен быть положительным числом.
     * @return Объект {@link LikeViewDto}, содержащий информацию о добавленном лайке.
     */
    @PostMapping("/posts/{postId}/like")
    public LikeViewDto likePost(@PathVariable
                                @NonNull Long postId) {
        long userId = userContext.getUserId();
        return likeService.likePost(postId, userId);
    }

    /**
     * Удаляет лайк с поста.
     *
     * @param postId Идентификатор поста, с которого удаляется лайк. Не может быть null и должен быть положительным числом.
     */
    @DeleteMapping("/posts/{postId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikePost(@PathVariable
                           @NonNull Long postId) {
        long userId = userContext.getUserId();
        likeService.unlikePost(postId, userId);
    }

    /**
     * Добавляет лайк к комментарию.
     *
     * @param commentId Идентификатор комментария, к которому добавляется лайк. Не может быть null и должен быть положительным числом.
     * @return Объект {@link LikeViewDto}, содержащий информацию о добавленном лайке.
     */
    @PostMapping("/comment/{commentId}/like")
    public LikeViewDto likeComment(@PathVariable
                                   @NonNull Long commentId) {
        long userId = userContext.getUserId();
        return likeService.likeComment(commentId, userId);
    }

    /**
     * Удаляет лайк с комментария.
     *
     * @param commentId Идентификатор комментария, с которого удаляется лайк. Не может быть null и должен быть положительным числом.
     */
    @DeleteMapping("/comment/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikeComment(@PathVariable
                              @NonNull Long commentId) {
        long userId = userContext.getUserId();
        likeService.unlikeComment(commentId, userId);
    }
}
