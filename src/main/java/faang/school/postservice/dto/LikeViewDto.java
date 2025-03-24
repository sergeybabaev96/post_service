package faang.school.postservice.dto;

import lombok.Data;

/**
 * DTO для представления информации о лайке.
 *
 * @author gulnaz21
 */
@Data
public class LikeViewDto {
    /**
     * Уникальный идентификатор лайка.
     */
    private long id;
    /**
     * Идентификатор пользователя, который поставил лайк.
     */
    private Long userId;
    /**
     * Идентификатор комментария, к которому относится лайк.
     * Может быть null, если лайк относится к посту.
     */
    private Long commentId;
    /**
     * Идентификатор поста, к которому относится лайк.
     * Может быть null, если лайк относится к комментарию.
     */
    private Long postId;
}
