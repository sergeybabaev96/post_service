package faang.school.postservice.config.post;

public final class PostServiceConstants {

    private PostServiceConstants() {
    }

    public static class ThreadPool {
        public static final int EXECUTOR_POOL_THREAD_NUMBER = 10;
    }

    public static class TimeOut {
        public static final int CORRECT_POSTS_FUTURES_TIMEOUT = 30;
        public static final int CHECK_SPELLING_TIMEOUT = 5;
    }

    public static class AwaitTermination {
        public static final int EXECUTOR_AWAIT_TERMINATION = 10;
    }

    public static class CheckSpellLanguage {
        public static final String DEFAULT_LANGUAGE = "en_US";
    }

    public static class CheckSpellCommand {
        public static final String CORRECTION_COMMAND = "autocorrect";
    }

    public static class CheckSpellRetry{
        public static final int MAX_RETRIES = 4;
        public static final long MAX_BACKOFF_DELAY = 8000L;
    }
}
