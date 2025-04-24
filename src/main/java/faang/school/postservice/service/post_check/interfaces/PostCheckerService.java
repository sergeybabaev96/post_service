package faang.school.postservice.service.post_check.interfaces;

import faang.school.postservice.model.Post;

import java.util.concurrent.CompletableFuture;

public interface PostCheckerService {
    CompletableFuture<Post> correctPost(Post post);

    CompletableFuture<String> checkSpellingWithRetry(String content);

    String parseCorrectedContent(String responseBody, String originalContent);
}
