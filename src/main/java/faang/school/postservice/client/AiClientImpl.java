package faang.school.postservice.client;

import faang.school.postservice.dto.gpt.ChatRequest;
import faang.school.postservice.dto.gpt.ChatResponse;
import faang.school.postservice.properties.AIProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientImpl implements AiClient {
    private final WebClient webClient;
    private final AIProperties aiProperties;

    @Retryable(backoff = @Backoff(delay = 21000))
    public ChatResponse chat(ChatRequest chatRequest) {
        return webClient.post()
                .uri(aiProperties.getChatUri())
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .onErrorMap(error -> {
                    log.error("Error during grammar check: {}", error.getMessage());
                    return new RuntimeException("API request failed", error);
                })
                .log()
                .block();
    }
}
