package faang.school.postservice.service.post_check.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.post.PostServiceConstants;
import faang.school.postservice.config.webclient.WebSpellHttpConfig;
import faang.school.postservice.exception.AIIntegrationException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.JsonNotReadException;
import faang.school.postservice.exception.PostNotCorrectedException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post_check.interfaces.PostCheckerService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCheckerServiceImpl implements PostCheckerService {
    private final PostRepository postRepository;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;
    private final HttpClient webSpellHttpClient;
    private final WebSpellHttpConfig webSpellHttpConfig;
    private final ExecutorService executor;

    @Override
    public CompletableFuture<Post> correctPost(Post post) {
        if (Objects.isNull(post) || Objects.isNull(post.getContent())) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Post or its content cannot be null"));
        }

        return checkSpellingWithRetry(post.getContent())
                .orTimeout(PostServiceConstants.CHECK_SPELLING_TIMEOUT, TimeUnit.SECONDS)
                .thenCompose(correctedContent -> {
                    log.info("Received corrected content: {}", correctedContent);
                    return CompletableFuture.supplyAsync(() ->
                            transactionTemplate.execute(status -> {
                                Post freshPost = postRepository.findById(post.getId())
                                        .orElseThrow(() -> new PostNotFoundException("Post not found: "
                                                + post.getId()));
                                freshPost.setContent(correctedContent);
                                freshPost.setUpdatedAt(LocalDateTime.now());
                                Post savedPost = postRepository.save(freshPost);
                                log.info("Successfully corrected post with id {}", savedPost.getId());
                                return savedPost;
                            }), executor);
                })
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to correct post with id {} after all retries, cause: {}",
                                post.getId(), throwable.getMessage());
                        throw new PostNotCorrectedException(post.getId(), "Failed after retries: "
                                + throwable.getMessage());
                    }
                });
    }

    @Override
    public CompletableFuture<String> checkSpellingWithRetry(String content) {
        if (content == null) {
            String exceptionMessage = "Content cannot be null.";
            DataValidationException e = new DataValidationException(exceptionMessage);
            log.error(exceptionMessage, e);
            throw e;
        }
        long contentSizeInBytes = content.getBytes().length;
        if (contentSizeInBytes > PostServiceConstants.MAX_CONTENT_SIZE) {
            String exceptionMessage = "Content size exceeds maximum allowed limit of "
                    + PostServiceConstants.MAX_CONTENT_SIZE + " bytes.";
            DataValidationException e = new DataValidationException(exceptionMessage);
            log.error(exceptionMessage, e);
            throw e;
        }

        if (content.length() < PostServiceConstants.SIZE_IN_BYTES_FOR_SINGLE_PROCESSING) {
            return processSingleSegment(content);
        }

        List<String> segments = splitIntoSegments(content);
        if (segments.isEmpty()) {
            return CompletableFuture.completedFuture(content);
        }

        List<CompletableFuture<String>> segmentFutures = segments.stream()
                .map(this::processSingleSegment)
                .toList();

        return CompletableFuture.allOf(segmentFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < segmentFutures.size(); i++) {
                        try {
                            result.append(segmentFutures.get(i).join());
                            if (i < segmentFutures.size() - 1) {
                                result.append(" ");
                            }
                        } catch (Exception e) {
                            log.error("Failed to process segment {}: {}", i, e.getMessage());
                            throw new AIIntegrationException("Failed to process segment: " + e.getMessage(), e);
                        }
                    }
                    return result.toString();
                });
    }

    @Override
    public String parseCorrectedContent(String responseBody, String originalContent) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode correctedNode = rootNode.path("corrected");
            if (correctedNode.isMissingNode() || correctedNode.isNull()) {
                log.warn("Field 'corrected' is missing or null in response: {}", responseBody);
                return originalContent;
            }
            if (correctedNode.isTextual()) {
                return correctedNode.asText();
            } else {
                String nodeType = correctedNode.getNodeType().toString();
                log.error("Field 'corrected' has unexpected type: {}, value: {}", nodeType, correctedNode);
                throw new JsonNotReadException(
                        "Field 'corrected' is not a string, found type: " + nodeType + ", value: " + correctedNode);
            }
        } catch (IOException e) {
            log.error("IO Exception while parsing response: {}", responseBody, e);
            throw new JsonNotReadException(
                    "IO Exception occurred while parsing response: " + responseBody + " to check spelling: "
                            + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while parsing response: {}", responseBody, e);
            throw new JsonNotReadException("Unexpected error in parsing response: "
                    + responseBody + " to check spelling: " + e.getMessage());
        }
    }

    private List<String> splitIntoSegments(String content) {
        List<String> segments = new ArrayList<>();
        Matcher matcher = PostServiceConstants.SENTENCE_PATTERN.matcher(content);
        int start = 0;

        while (matcher.find()) {
            int end = matcher.start();
            String segment = content.substring(start, end).trim();
            if (!segment.isEmpty()) {
                segments.add(segment);
            }
            start = matcher.end();
        }

        if (start < content.length()) {
            String lastSegment = content.substring(start).trim();
            if (!lastSegment.isEmpty()) {
                segments.add(lastSegment);
            }
        }

        return segments;
    }

    @Retryable(retryFor = {AIIntegrationException.class}, maxAttempts = PostServiceConstants.MAX_RETRIES,
            backoff = @Backoff(maxDelay = PostServiceConstants.MAX_BACKOFF_DELAY,
                    multiplier = PostServiceConstants.RETRY_MULTIPLIER))
    private CompletableFuture<String> processSingleSegment(String segment) {
        Map<String, String> requestBody = Map.of(
                "cmd", PostServiceConstants.CORRECTION_COMMAND,
                "lang", PostServiceConstants.DEFAULT_LANGUAGE,
                "text", segment
        );

        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(new AIIntegrationException(
                    "Failed to serialize request: " + e.getMessage(), e));
        }

        try {
            return CompletableFuture.supplyAsync(() -> {
                HttpRequest request = getWebSpellHttpRequest(jsonRequest);
                try {
                    HttpResponse<String> response = webSpellHttpClient.send(
                            request, HttpResponse.BodyHandlers.ofString());
                    String responseBody = response.body();
                    if (responseBody == null) {
                        String exceptionMessage = "Received null response from proofreader for segment: " + segment;
                        AIIntegrationException e = new AIIntegrationException(exceptionMessage);
                        log.error(exceptionMessage, e);
                        throw e;
                    }
                    int statusCode = response.statusCode();
                    if (statusCode != PostServiceConstants.SUCCESSFULLY_STATUS_CODE) {
                        String exceptionMessage = "Unexpected code " + statusCode + " received from the proofreader";
                        AIIntegrationException e = new AIIntegrationException(exceptionMessage);
                        log.error(exceptionMessage, e);
                        throw e;
                    }
                    return parseCorrectedContent(responseBody, segment);
                } catch (IOException | InterruptedException e) {
                    if (e instanceof InterruptedException) {
                        log.warn("Thread: \"{}\" has interrupted in: {}", Thread.currentThread(), LocalDateTime.now());
                        Thread.currentThread().interrupt();
                    }
                    String errorType = switch (e.getClass().getSimpleName()) {
                        case "SocketTimeoutException" -> "Timeout waiting for response";
                        case "ConnectException" -> "Cannot connect to server";
                        case "IOException" -> "IO error";
                        case "InterruptedException" -> "Interrupted during spelling check";
                        default -> "Unknown error";
                    };
                    log.error("{} during spelling check: {}, {}, {}", errorType, segment, e.getMessage(), e.toString());
                    throw new AIIntegrationException(errorType + ": " + segment + ", " + e.getMessage(), e);
                }
            }, executor);
        } catch (RejectedExecutionException e) {
            log.error("Executor rejected task for segment: {}", segment);
            throw new AIIntegrationException("Failed to schedule task for segment: " + segment, e);
        }
    }

    private HttpRequest getWebSpellHttpRequest(String jsonRequest) {
        String apiUrl = webSpellHttpConfig.getWebSpellApiUrl();
        URI uri = URI.create(apiUrl);
        String contentType = webSpellHttpConfig.getWebSpellApiContentType();
        String apiKey = webSpellHttpConfig.getWebSpellApiKey();
        String apiHost = webSpellHttpConfig.getWebSpellApiHost();

        validateWebSpellHttpRequest(jsonRequest, apiUrl, contentType, apiKey, apiHost);

        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", contentType)
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", apiHost)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    }

    private void validateWebSpellHttpRequest(String jsonRequest, String apiUrl, String contentType,
                                             String apiKey, String apiHost) {

        Objects.requireNonNull(jsonRequest, "JSON request body must not be null");

        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw logAndThrowAIIntegrationException("WebSpell API URL is null or empty");
        }

        if (contentType == null || contentType.trim().isEmpty()) {
            log.warn("WebSpell API Content-Type is null or empty, using default: application/json");
            contentType = "application/json";
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw logAndThrowAIIntegrationException("WebSpell API key is null or empty");
        }

        if (apiHost == null || apiHost.trim().isEmpty()) {
            throw logAndThrowAIIntegrationException("WebSpell API host is null or empty");
        }
    }

    private AIIntegrationException logAndThrowAIIntegrationException(String message) {
        log.error(message);
        return new AIIntegrationException(message);
    }
}
