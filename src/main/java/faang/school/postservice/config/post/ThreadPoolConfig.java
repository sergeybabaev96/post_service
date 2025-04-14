package faang.school.postservice.config.post;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    public static final int POST_PUBLISH_CORE_POOL_SIZE = 10;
    public static final int POST_PUBLISH_MAX_POOL_SIZE = 10;
    public static final int QUEUE_HOLDING_TASKS_SIZE = 100;

    @Bean(name = "threadPoolExecutor", destroyMethod = "shutdown")
    public ExecutorService postPublishPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                POST_PUBLISH_CORE_POOL_SIZE,
                POST_PUBLISH_MAX_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(QUEUE_HOLDING_TASKS_SIZE),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        return executor;
    }
}
