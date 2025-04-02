package faang.school.postservice.config.post;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {
    public static final int POST_PUBLISH_POOL_SIZE = 10;

    @Bean(name = "threadPoolExecutor", destroyMethod = "shutdown")
    public ExecutorService postPublishPool() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(POST_PUBLISH_POOL_SIZE);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy()); // Отклоняем новые задачи, если очередь полна
        return executor;
    }
}
