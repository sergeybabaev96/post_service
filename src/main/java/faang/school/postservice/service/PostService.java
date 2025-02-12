package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.corrector.PostCorrector;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostCorrector postCorrector;
    private final UserContext userContext;

    @Scheduled(cron = "${spell-service.cron}")
    @Async("spellServicePool")
    public void correctAllUnpublishedPosts() {
//        userContext.setUserId(1);
        log.info("Начало запланированного события");
        List<Post> posts = postRepository.findReadyToPublish();
//        String text = "я люблю каров";
//        postCorrector.autocorrect(text);
        posts.forEach(postCorrector::autocorrect);
        postRepository.saveAll(posts);
        log.info("Конец запланированного события");
//        postCorrector.autoCorrect(text);
    }

    public Post findById(@NotNull Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("Пост с id=%d не найден", id)));
    }
}
