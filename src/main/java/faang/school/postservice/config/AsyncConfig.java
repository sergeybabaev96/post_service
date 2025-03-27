package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    @Value("${spring.kafka.producer.events-sender.core-pool-size}")
    private int kafkaSenderCorePoolSize;

    @Value("${spring.kafka.producer.events-sender.max-pool-size}")
    private int kafkaSenderMaxPoolSize;

    @Value("${spring.kafka.producer.events-sender.queue-capacity}")
    private int kafkaSenderQueueCapacity;

    @Value("${spring.kafka.producer.events-sender.thread-name-prefix}")
    private String kafkaSenderPrefix;

    @Bean
    public ThreadPoolTaskExecutor kafkaSendEventThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(kafkaSenderCorePoolSize);
        executor.setMaxPoolSize(kafkaSenderMaxPoolSize);
        executor.setQueueCapacity(kafkaSenderQueueCapacity);
        executor.setThreadNamePrefix(kafkaSenderPrefix);
        return executor;
    }
}
