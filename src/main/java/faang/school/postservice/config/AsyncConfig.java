package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

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

    @Value("${spring.data.redis.cache.post.core-pool-size}")
    private int cachePostCorePoolSize;

    @Value("${spring.data.redis.cache.post.max-pool-size}")
    private int cachePostMaxPoolSize;

    @Value("${spring.data.redis.cache.post.queue-capacity}")
    private int cachePostQueueCapacity;

    @Value("${spring.data.redis.cache.post.thread-name-prefix}")
    private String cachePostThreadNamePrefix;
  
    @Bean
    public ThreadPoolTaskExecutor kafkaSendEventThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(kafkaSenderCorePoolSize);
        executor.setMaxPoolSize(kafkaSenderMaxPoolSize);
        executor.setQueueCapacity(kafkaSenderQueueCapacity);
        executor.setThreadNamePrefix(kafkaSenderPrefix);
        return executor;
    }

    @Bean(name = "cachePostExecutor")
    public Executor cachePostExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cachePostCorePoolSize);
        executor.setMaxPoolSize(cachePostMaxPoolSize);
        executor.setQueueCapacity(cachePostQueueCapacity);
        executor.setThreadNamePrefix(cachePostThreadNamePrefix);
        executor.initialize();
        return executor;
    }
}
