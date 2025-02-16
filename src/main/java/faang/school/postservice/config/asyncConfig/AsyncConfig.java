package faang.school.postservice.config.asyncConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Value("${post.ad-remover.threads-count}")
    private int poolSize;

    @Bean
    public ExecutorService adRemoverThreadPool() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
