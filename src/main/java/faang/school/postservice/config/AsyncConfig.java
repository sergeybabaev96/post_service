package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "schedulers.config")
@EnableAsync
@Getter
@Setter
public class AsyncConfig {
    private int corePoolSize;
    private int maximumPoolSize;
    private int queueCapacity;
    @Value("${schedulers.config.moderation.threadNamePrefix}")
    private String moderationThreadNamePrefix;

    @Bean(name = "commonTaskExecutor")
    public ThreadPoolTaskExecutor moderationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(moderationThreadNamePrefix);
        executor.initialize();
        return executor;
    }
}