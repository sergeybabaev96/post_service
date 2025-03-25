package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс `PostCreateDto` используется для передачи данных, необходимых для создания нового поста.
 * Содержит минимальный набор полей, которые должны быть предоставлены при создании поста.
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Идентификатор автора ({@link #authorId})</li>
 *     <li>Идентификатор проекта ({@link #projectId})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 * @version 1.0
 */
@Data
public class PostCreateDto {
    @NotBlank(message = "Content is not blank")
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime scheduledAt;
}
