package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс `PostUpdateDto` используется для передачи данных, необходимых для обновления существующего поста.
 * Содержит минимальный набор полей, которые могут быть изменены при обновлении поста.
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Флаг публикации ({@link #published})</li>
 *     <li>Флаг удаления поста ({@link #deleted})</li>
 *     <li>Список идентификаторов ресурсов ({@link #resourceIds})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Data
public class PostUpdateDto {
    @NotBlank(message = "Content is not blank")
    private String content;
    private boolean published;
    private boolean deleted;
    private List<Long> resourceIds;
    private LocalDateTime scheduledAt;
}
