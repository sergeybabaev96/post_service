package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ConfigurationProperties(prefix = "publish-scheduler.config")
@Getter
@Setter
public class ThreadPoolConfig {
    private int assignedTreads;

    @Bean(name = "publishingThreadPool")
    public ExecutorService getPublishingThreadPool() {
        return Executors.newFixedThreadPool(assignedTreads);
    }
}
