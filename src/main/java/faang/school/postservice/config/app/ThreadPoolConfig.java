package faang.school.postservice.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService postPublishingExecutor(){
        return Executors.newFixedThreadPool(10);
    }
}
