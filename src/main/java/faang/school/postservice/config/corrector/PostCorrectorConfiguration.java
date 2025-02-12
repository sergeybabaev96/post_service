package faang.school.postservice.config.corrector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PostCorrectorConfiguration {

    @Value("${spell-service.thread-pool-size}")
    private int pollSize;

    @Bean
    public ExecutorService spellServicePool() {
        return Executors.newFixedThreadPool(pollSize);
    }
}
