package faang.school.postservice.config;

import faang.school.postservice.properties.AIProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiConfig {

    @Bean
    public WebClient aiWebClient(AIProperties aiProperties) {
        return WebClient.builder()
                .baseUrl(aiProperties.getBaseUrl())
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.setBearerAuth(aiProperties.getApiKey());
                })
                .build();
    }

}
