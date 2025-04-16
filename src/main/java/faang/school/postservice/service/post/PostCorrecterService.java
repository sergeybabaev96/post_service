package faang.school.postservice.service.post;

import faang.school.postservice.client.speller.YandexSpellerClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.speller.SpellerDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCorrecterService {
    private static final String CORRECTING_POST_FORM = "{} correct post {}";

    private final YandexSpellerClient yandexSpellerClient;
    private final PostService postService;

    @Retryable(retryFor = { FeignException.class }, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void correctingSpellingPost(PostDto postDto) {
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
