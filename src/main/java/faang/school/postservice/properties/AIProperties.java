package faang.school.postservice.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("post-service.ai")
public class AIProperties {
    @NotBlank(message = "API-ключ обязателен для работы с AI")
    private String apiKey;
    @NotBlank
    private String grammarPrompt;
    @NotBlank
    private String chatUri;
    private String baseUrl;
    private String model;
}
