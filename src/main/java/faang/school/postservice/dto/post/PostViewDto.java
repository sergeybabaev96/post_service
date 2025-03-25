package faang.school.postservice.dto.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс `PostViewDto` используется для передачи данных, необходимых для отображения информации о посте.
 * Содержит основные поля поста, а также идентификаторы связанных сущностей (лайки, комментарии, ресурсы и т.д.).
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Уникальный идентификатор поста ({@link #id})</li>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Идентификатор автора ({@link #authorId})</li>
 *     <li>Идентификатор проекта ({@link #projectId})</li>
 *     <li>Количество лайков лайков ({@link #totalLikes})</li>
 *     <li>Список идентификаторов комментариев ({@link #commentIds})</li>
 *     <li>Список идентификаторов ресурсов ({@link #resourcesIds})</li>
 *     <li>Идентификатор рекламного объявления ({@link #adId})</li>
 *     <li>Флаг публикации ({@link #published})</li>
 *     <li>Дата публикации ({@link #publishedAt})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 *     <li>Флаг удаления поста ({@link #deleted})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Data
public class PostViewDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long totalLikes;
    private List<Long> commentIds;
    private List<Long> resourcesIds;
    private Long adId;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
}
