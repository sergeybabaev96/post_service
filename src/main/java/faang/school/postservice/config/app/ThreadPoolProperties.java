package faang.school.postservice.config.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "postservice.thread-pool")
public class ThreadPoolProperties {
    private int size;
}