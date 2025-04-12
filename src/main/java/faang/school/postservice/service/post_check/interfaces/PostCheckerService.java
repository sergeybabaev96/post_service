package faang.school.postservice.service.post_check.interfaces;

import faang.school.postservice.model.Post;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface PostCheckerService {
    CompletableFuture<Post> correctPost(Post post, ExecutorService executor);

    CompletableFuture<String> checkSpellingWithRetry(String content, ExecutorService executor);

    String parseCorrectedContent(String responseBody, String originalContent);
}
