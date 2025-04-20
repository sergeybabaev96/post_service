package faang.school.postservice.service.post_check.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import faang.school.postservice.config.webclient.WebSpellHttpConfig;
import faang.school.postservice.exception.AIIntegrationException;
import faang.school.postservice.exception.PostNotCorrectedException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PostCheckerServiceImplTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpClient webSpellHttpClient;

    @Mock
    private WebSpellHttpConfig webSpellHttpConfig;

    private ThreadPoolTaskExecutor executor;

    @InjectMocks
    private PostCheckerServiceImpl postCheckerService;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setContent("Posssst");
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setKeepAliveSeconds(0);
        executor.setQueueCapacity(100);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();

        postCheckerService = new PostCheckerServiceImpl(
                postRepository, transactionTemplate, objectMapper, webSpellHttpClient,
                webSpellHttpConfig, executor);
    }

    @Test
    void testCorrectPostSuccessfully() throws IOException, ExecutionException, InterruptedException {
        String correctedContent = "Post";

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"corrected\":\"Post\"}");

        JsonNode mockJsonNode = mock(JsonNode.class);
        JsonNode correctedNode = mock(JsonNode.class);
        when(mockJsonNode.path("corrected")).thenReturn(correctedNode);
        when(correctedNode.isMissingNode()).thenReturn(false);
        when(correctedNode.getNodeType()).thenReturn(JsonNodeType.STRING);
        when(correctedNode.asText()).thenReturn(correctedContent);

        when(webSpellHttpConfig.getWebSpellApiUrl())
                .thenReturn("https://webspellchecker-webspellcheckernet.p.rapidapi.com/api");
        when(webSpellHttpConfig.getWebSpellApiContentType()).thenReturn("application/json");
        when(webSpellHttpConfig.getWebSpellApiKey()).thenReturn("348d2925famsh95c7436e81d45c0p1eeb4ajsn1347b2ce620a");
        when(webSpellHttpConfig.getWebSpellApiHost()).thenReturn("webspellchecker-webspellcheckernet.p.rapidapi.com");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(transactionTemplate.execute(argThat(Objects::nonNull))).thenAnswer(invocation -> {
            TransactionCallback<Post> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        when(webSpellHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"text\":\"Posssst\"}");
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);

        CompletableFuture<Post> future = postCheckerService.correctPost(post);
        future.get();

        verify(postRepository).save(any(Post.class));
        assertEquals(correctedContent, post.getContent());
        assertNotNull(post.getUpdatedAt());
        executor.shutdown();
    }

    @Test
    void testCorrectPost_whenThrowsPostNotFoundException() throws IOException, InterruptedException {
        String notCorrectedContent = "Posssst";
        when(webSpellHttpConfig.getWebSpellApiUrl())
                .thenReturn("https://webspellchecker-webspellcheckernet.p.rapidapi.com/api");
        when(webSpellHttpConfig.getWebSpellApiContentType()).thenReturn("application/json");
        when(webSpellHttpConfig.getWebSpellApiKey()).thenReturn("348d2925famsh95c7436e81d45c0p1eeb4ajsn1347b2ce620a");
        when(webSpellHttpConfig.getWebSpellApiHost()).thenReturn("webspellchecker-webspellcheckernet.p.rapidapi.com");
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"text\": \"" + post.getContent() + "\"}");
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("{\"text\": \"" + notCorrectedContent + "\"}");
        when(webSpellHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        CompletableFuture<Post> result = postCheckerService.correctPost(post);

        CompletionException completionException = assertThrows(CompletionException.class, result::join,
                "Expected CompletionException to be thrown");
        Throwable cause = completionException.getCause();
        assertInstanceOf(AIIntegrationException.class, cause,
                "Cause of CompletionException should be AIIntegrationException");
        assertEquals("Unexpected code 404 received from the proofreader", cause.getMessage(),
                "Cause message should match the AIIntegrationException message");
        System.out.println("CompletionException cause: " + cause.getClass().getSimpleName()
                + " - " + cause.getMessage());
        Throwable[] suppressed = completionException.getSuppressed();
        assertEquals(1, suppressed.length, "Should have one suppressed exception");
        assertInstanceOf(PostNotCorrectedException.class, suppressed[0],
                "Suppressed exception should be PostNotCorrectedException");
        assertEquals("Unable to correct post with id 1", suppressed[0].getMessage(),
                "Suppressed exception message should match");
        System.out.println("Suppressed exception: " + suppressed[0].getClass().getSimpleName()
                + " - " + suppressed[0].getMessage());
        executor.shutdown();
    }

    @Test
    void testCheckSpellingWithRetrySuccessfully() throws Exception {
        String content = "Posssst";
        String correctedContent = "Post";
        when(webSpellHttpConfig.getWebSpellApiUrl())
                .thenReturn("https://webspellchecker-webspellcheckernet.p.rapidapi.com/api");
        when(webSpellHttpConfig.getWebSpellApiContentType()).thenReturn("application/json");
        when(webSpellHttpConfig.getWebSpellApiKey()).thenReturn("348d2925famsh95c7436e81d45c0p1eeb4ajsn1347b2ce620a");
        when(webSpellHttpConfig.getWebSpellApiHost()).thenReturn("webspellchecker-webspellcheckernet.p.rapidapi.com");
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"text\":\"Posssst\"}");
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"corrected\":\"Post\"}");
        when(webSpellHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);
        ObjectMapper realObjectMapper = new ObjectMapper();
        JsonNode expectedJsonNode = realObjectMapper.readTree("{\"corrected\":\"Post\"}");
        when(objectMapper.readTree("{\"corrected\":\"Post\"}")).thenReturn(expectedJsonNode);

        CompletableFuture<String> future = postCheckerService.checkSpellingWithRetry(content);
        String result = future.join();

        assertEquals(correctedContent, result);
        executor.shutdown();
    }

    @Test
    void testCheckSpellingWithRetry_whenFailure() throws AIIntegrationException, IOException, InterruptedException {
        String content = "Posssst";

        when(webSpellHttpConfig.getWebSpellApiUrl())
                .thenReturn("https://webspellchecker-webspellcheckernet.p.rapidapi.com/api");
        when(webSpellHttpConfig.getWebSpellApiContentType()).thenReturn("application/json");
        when(webSpellHttpConfig.getWebSpellApiKey()).thenReturn("348d2925famsh95c7436e81d45c0p1eeb4ajsn1347b2ce620a");
        when(webSpellHttpConfig.getWebSpellApiHost()).thenReturn("webspellchecker-webspellcheckernet.p.rapidapi.com");
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"text\":\"Posssst\"}");
        HttpResponse<String> failResponse = mock(HttpResponse.class);
        when(failResponse.statusCode()).thenReturn(500);
        when(failResponse.body()).thenReturn("{\"error\":\"Server error\"}");
        when(webSpellHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(failResponse);

        CompletableFuture<String> future = postCheckerService.checkSpellingWithRetry(content);

        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(AIIntegrationException.class, exception.getCause());
        assertEquals("Unexpected code 500 received from the proofreader", exception.getCause().getMessage());
        verify(webSpellHttpClient, times(1))
                .send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
        executor.shutdown();
    }

    @Test
    void testParseCorrectedContentSuccessfully() throws Exception {
        String responseBody = "{\"corrected\":\"Post\"}";
        String originalContent = "Posssst";
        String correctedContent = "Post";
        ObjectMapper realObjectMapper = new ObjectMapper();
        JsonNode expectedJsonNode = realObjectMapper.readTree(responseBody);
        when(objectMapper.readTree(responseBody)).thenReturn(expectedJsonNode);

        String result = postCheckerService.parseCorrectedContent(responseBody, originalContent);

        assertEquals(correctedContent, result);
    }
}
