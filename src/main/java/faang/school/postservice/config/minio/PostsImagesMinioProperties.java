package faang.school.postservice.config.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "image-for-post")
public class PostsImagesMinioProperties {
    private int maxHorizontalHeight;
    private int maxHorizontalWidth;
    private int maxSquareDimensions;
    private int maxImageSize;
    private double outputQuality;
    private String bucketName;
    private String folderName;
}
