package faang.school.postservice.correcter;

import faang.school.postservice.service.post.PostCorrecterService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private static final String CORRECTING_POSTS_FORM = "{} correcting the spelling of the posts";

    private final PostService postService;
    private final PostCorrecterService postCorrecterService;

    @Async("postSpellingCorrecter")
    @Scheduled(cron = "${cron.correct_post}")
    public void correctingSpellingOfPosts() {
        log.info(CORRECTING_POSTS_FORM, "Start");
        postService.getAllDraftPosts().forEach(postCorrecterService::correctingSpellingPost);
        log.info(CORRECTING_POSTS_FORM, "Finish");
    }

}
