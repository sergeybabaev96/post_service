package faang.school.postservice.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "services.s3")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
}
