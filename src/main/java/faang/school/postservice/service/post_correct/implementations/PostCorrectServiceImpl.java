package faang.school.postservice.service.post_correct.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.post.PostServiceConstants;
import faang.school.postservice.config.webclient.WebSpellHttpConfig;
import faang.school.postservice.exception.AIIntegrationException;
import faang.school.postservice.exception.JsonNotReadException;
import faang.school.postservice.exception.PostNotCorrectedException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post_correct.interfaces.PostCorrectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class PostCorrectServiceImpl implements PostCorrectService {
    private final PostRepository postRepository;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;
    private final HttpClient webSpellHttpClient;
    private final WebSpellHttpConfig webSpellHttpConfig;

    @Override
    public CompletableFuture<Void> correctPost(Post post, ExecutorService executor) {
        if (Objects.isNull(post) || Objects.isNull(post.getContent())) {
            throw new IllegalArgumentException("Post or its content cannot be null");
        }
        return checkSpellingWithRetry(post.getContent())
                .orTimeout(PostServiceConstants.TimeOut.CHECK_SPELLING_TIMEOUT, TimeUnit.SECONDS)
                .thenCompose(correctedContent -> {
                    log.info("Received corrected content: {}", correctedContent);
                    return CompletableFuture.supplyAsync(() ->
                            transactionTemplate.execute(status -> {
                                Post freshPost = postRepository.findById(post.getId())
                                        .orElseThrow(() -> new PostNotFoundException("Post not found: " + post.getId()));
                                freshPost.setContent(correctedContent);
                                freshPost.setUpdatedAt(LocalDateTime.now());
                                Post savedPost = postRepository.save(freshPost);
                                log.info("Successfully corrected post with id {}", savedPost.getId());
                                return savedPost;
                            }), executor);
                })
                .thenRun(() -> {
                })
                .exceptionally(throwable -> {
                    log.error("Failed to correct post with id {} after all retries, cause: {}",
                            post.getId(), throwable.getMessage());
                    throw new PostNotCorrectedException(post.getId(), "Failed after retries: " + throwable.getMessage());
                });
    }

    @Override
    @Retryable(retryFor = {AIIntegrationException.class}, maxAttempts = PostServiceConstants.CheckSpellRetry.MAX_RETRIES,
            backoff = @Backoff(maxDelay = PostServiceConstants.CheckSpellRetry.MAX_BACKOFF_DELAY, multiplier = 2)
    )
    public CompletableFuture<String> checkSpellingWithRetry(String content) {
        Map<String, String> requestBody = Map.of(
                "cmd", PostServiceConstants.CheckSpellCommand.CORRECTION_COMMAND,
                "lang", PostServiceConstants.CheckSpellLanguage.DEFAULT_LANGUAGE,
                "text", content
        );
        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new AIIntegrationException("Failed to serialize request: " + e.getMessage(), e);
        }
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = getWebSpellHttpRequest(jsonRequest);
            try {
                HttpResponse<String> response = webSpellHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();
                if (responseBody == null) {
                    throw new AIIntegrationException(
                            "Received null response from proofreader for checking content " + content);
                }
                int statusCode = response.statusCode();
                if (statusCode != 200) {
                    throw new AIIntegrationException("Unexpected code " + statusCode + " received from the proofreader");
                }
                return parseCorrectedContent(responseBody, content);
            } catch (IOException | InterruptedException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                String errorType = switch (e.getClass().getSimpleName()) {
                    case "SocketTimeoutException" -> "Timeout waiting for response";
                    case "ConnectException" -> "Cannot connect to server";
                    case "IOException" -> "IO error";
                    case "InterruptedException" -> "Interrupted during spelling check";
                    default -> "Unknown error";
                };
                log.error("{} before spelling check: {}, {}, {}", errorType, content, e.getMessage(), e.toString());
                throw new AIIntegrationException(errorType + ": " + content + "," + e.getMessage(), e);
            }
        });
    }

    @Override
    public String parseCorrectedContent(String responseBody, String originalContent) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode correctedNode = rootNode.path("corrected");
            if (correctedNode.isMissingNode() || correctedNode.isNull()) {
                return originalContent;
            }
            return correctedNode.asText();
        } catch (IOException e) {
            throw new JsonNotReadException(
                    "IO Exception occurred while parsing response to check spelling: " + e.getMessage());
        } catch (Exception e) {
            throw new JsonNotReadException("Unexpected error in parsing to check spelling: " + e.getMessage());
        }
    }

    private HttpRequest getWebSpellHttpRequest(String jsonRequest) {
        return HttpRequest.newBuilder()
                .uri(URI.create(webSpellHttpConfig.getWebSpellApiUrl()))
                .header("Content-Type",  webSpellHttpConfig.getWebSpellApiContentType())
                .header("x-rapidapi-key", webSpellHttpConfig.getWebSpellApiKey())
                .header("x-rapidapi-host", webSpellHttpConfig.getWebSpellApiHost())
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    }
}
