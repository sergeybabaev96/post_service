package faang.school.postservice.config.app;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolProperties properties;

    @Bean
    public ExecutorService postPublishingExecutor(){
        return Executors.newFixedThreadPool(properties.getSize());
    }
}
