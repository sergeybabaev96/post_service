package faang.school.postservice.exception;

import faang.school.postservice.model.Post;

import java.util.List;

public class PostModerationException extends RuntimeException {
    private final List<Post> failedPosts;

    public PostModerationException(String message, List<Post> failedPosts) {
        super(message);
        this.failedPosts = failedPosts;
    }

    public List<Post> getFailedPosts() {
        return failedPosts;
    }
}
