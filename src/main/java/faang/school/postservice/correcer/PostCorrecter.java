package faang.school.postservice.correcer;

import faang.school.postservice.client.speller.YandexSpellerClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.speller.SpellerDto;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private static final String CORRECTING_POSTS_FORM = "{} correcting the spelling of the posts";
    private static final String CORRECTING_POST_FORM = "{} correct post {}";

    private final YandexSpellerClient yandexSpellerClient;
    private final PostService postService;

    @Async("postSpellingCorrecter")
    @Scheduled(cron = "${cron.correct_post}")
    public void correctingSpellingOfPosts() {
        log.info(CORRECTING_POSTS_FORM, "Start");
        postService.getAllPosts().forEach(this::correctingSpellingPost);
        log.info(CORRECTING_POSTS_FORM, "Finish");
    }

    @Retryable(retryFor = FeignException.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void correctingSpellingPost(PostDto postDto) {
        log.info(CORRECTING_POST_FORM, "Start", postDto);
        StringBuilder builder = new StringBuilder(postDto.getContent());

        List<SpellerDto> spellers = yandexSpellerClient.checkSpelling(postDto.getContent());
        log.info("get spells {}", spellers);
        Collections.reverse(spellers);
        log.info("reverse spells {}", spellers);

        spellers.forEach(speller ->
                builder.replace(speller.getPos(), speller.getLen(), speller.getS().get(0)));

        postDto.setContent(builder.toString());
        postService.updatePost(postDto.getId(), postDto);
        log.info(CORRECTING_POST_FORM, "Finish", postDto);
    }
}
