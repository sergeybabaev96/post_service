package faang.school.postservice.jobs;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;
    private final PostRepository postRepository;
    @Value("${post-service.jobs.post-corrector.batch-count}")
    private int batchCount;

    @Scheduled(cron = "${post-service.jobs.post-corrector.cron}")
    @SchedulerLock(name = "postCorrecterJob")
    public void postCorrecterJob() {
        log.info("Start post correcter job");
        if (batchCount == 0) {
            log.info("BatchCount is 0, skipping job");
            return;
        }
        List<Post> notPublishedPosts = postRepository.findPostsByPublishedIsFalseAndAiCheckedIsFalse(PageRequest.of(0, batchCount));
        log.info("Found {} posts", notPublishedPosts.size());
        notPublishedPosts.forEach(postService::grammarCorrectionPost);

        log.info("Finish post correcter job");
    }
}
