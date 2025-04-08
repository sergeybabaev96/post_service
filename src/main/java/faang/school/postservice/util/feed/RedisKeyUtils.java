package faang.school.postservice.util.feed;

public final class RedisKeyUtils {

    private RedisKeyUtils() {
    }

    public static String feedCacheKey(String prefix, Long userId) {
        return prefix + userId;
    }
}