package faang.school.postservice.config.post;

import java.util.regex.Pattern;

public interface PostServiceConstants {
    int EXECUTOR_POOL_THREAD_NUMBER = 10;
    int CORRECT_POSTS_FUTURES_TIMEOUT = 30;
    int CHECK_SPELLING_TIMEOUT = 5;
    long MAX_CONTENT_SIZE = 10 * 1024 * 1024;
    Pattern SENTENCE_PATTERN = Pattern.compile("(?<=[.!?])\\s+");
    int EXECUTOR_AWAIT_TERMINATION = 10;
    String DEFAULT_LANGUAGE = "en_US";
    String CORRECTION_COMMAND = "autocorrect";
    int MAX_RETRIES = 4;
    long MAX_BACKOFF_DELAY = 8000L;
    int RETRY_MULTIPLIER = 2;
    int SIZE_IN_BYTES_FOR_SINGLE_PROCESSING = 1000;
    int SUCCESSFULLY_STATUS_CODE = 200;
}
