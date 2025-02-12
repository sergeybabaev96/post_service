package faang.school.postservice.jobs;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostRepository postRepository;

    @Scheduled(cron = "${jobs.post-corrector.cron}")
    public void postCorrecterJob() {
        log.info("Start post correcter job");
        List<Post> notPublished = postRepository.findNotPublished();
        log.info("Found {} posts", notPublished.size());

        notPublished.forEach(this::checkAI);
        log.info("Finish post correcter job");
    }

    @Async("aICheckExecutor")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void checkAI(Post post) {
        log.info("Checking correcting post id = {}", post.getId());

        post.getContent();
    }
}
