package faang.school.postservice.schedule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadPoolConfig {

    @Value("${thread_pool.max_threads}")
    private int threadsNumber;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadsNumber);
    }
}
