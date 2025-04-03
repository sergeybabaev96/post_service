package faang.school.postservice.banner;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentBanner {
    private final PostService postService;

    @Scheduled(cron = "${app.ban-users-action-schedule}")
    public void banUsersIfRequired() {
        postService.banUsersIfRequired();
    }
}
