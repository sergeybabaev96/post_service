package faang.school.postservice.service;

import faang.school.postservice.api.PerspectiveAPI;
import faang.school.postservice.exception.PostModerationException;
import faang.school.postservice.exception.PerspectiveAPIException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    public static final String MODERATION_FAIL_EXCEPTION = "Moderation failed for post";
    private final PostRepository postRepository;
    private final PerspectiveAPI perspectiveAPI;
    private final ExecutorService moderationExecutor;
    private Page<Post> page;

    @Value("${moderation.batch.size}")
    private int pageSize;

    @Async
    public void moderatePosts() {
        log.info("Starting posts moderation");
        int pageNumber = 0;

        do {
            page = postRepository.findByVerifiedDateIsNull(
                    PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending())
            );

            CompletableFuture.runAsync(() -> moderateBatch(page.getContent()), moderationExecutor)
                    .join();

            pageNumber++;
        } while (page.hasNext());
    }

    @Retryable(retryFor = PostModerationException.class, backoff = @Backoff(
            delayExpression = "${moderation.retry.delay}",
            multiplierExpression = "${moderation.retry.multiplier}"),
            maxAttemptsExpression = "${moderation.retry.max-attempts}")
    public void moderateBatch(List<Post> batch) {
        List<Post> failedPosts = new ArrayList<>();

        for (Post post : batch) {
            try {
                boolean isToxic = perspectiveAPI.isContentToxic(post.getContent());

                post.setVerified(!isToxic);
                post.setVerifiedDate(LocalDateTime.now());

                postRepository.save(post);
                log.info("Post {} moderated. Toxic: {}", post.getId(), isToxic);
            } catch (PerspectiveAPIException e) {
                failedPosts.add(post);
                log.error("Failed to moderate post: {}", post.getId());
            }
        }

        if (!failedPosts.isEmpty()) {
            String errorMessage = String.format(
                    "Failed to moderate %d posts. Failed IDs: %s.",
                    failedPosts.size(),
                    failedPosts.stream().map(Post::getId).collect(Collectors.toList())
            );
            throw new PostModerationException(errorMessage, failedPosts);
        }

        log.info("All post have been moderated");
    }

    @Recover
    public void recoverModeration(PostModerationException e) {
        List<Post> failedPosts = e.getFailedPosts();

        log.error(
                "Moderation failed for {} posts after all retries. Failed IDs: {}",
                failedPosts.size(),
                failedPosts.stream().map(Post::getId).collect(Collectors.toList())
        );
    }
}
