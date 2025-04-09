package faang.school.postservice.moderator;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${app.rude-words-analyzer-schedule}")
    public void moderateComments() {
        commentService.moderateComments();
    }
}
