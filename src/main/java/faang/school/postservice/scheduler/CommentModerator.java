package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Компонент для периодической модерации комментариев.
 * <p>
 * Запускается автоматически по расписанию.
 * Логика модерации делегируется сервису {@link CommentService}.
 * </p>
 *
 * @see CommentService#moderateUnverifiedComment()
 */
@Component
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentService commentService;

    /**
     * Метод для периодической модерации неверифицированных комментариев.
     * <p>
     * Запускается автоматически по расписанию.
     * Логика модерации делегируется сервису {@link CommentService}.
     * </p>
     *
     * @see CommentService#moderateUnverifiedComment() ()
     */
    @Scheduled(cron = "${moderation.cron.expression}")
    public void moderatePost() {
        commentService.moderateUnverifiedComment();
    }
}