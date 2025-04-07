package faang.school.postservice.minio;

import faang.school.postservice.exception.minio_exceptions.BucketNotFoundException;
import faang.school.postservice.exception.minio_exceptions.MinioRemovingFileException;
import faang.school.postservice.exception.minio_exceptions.MinioUploadingFileException;
import faang.school.postservice.messages.ExceptionMessages;
import faang.school.postservice.model.Resource;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Configuration
public class MinioConfig {
    @Value("${services.minio.endpoint}")
    private String endpoint;

    @Value("${services.minio.access-key}")
    private String accessKey;

    @Value("${services.minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
