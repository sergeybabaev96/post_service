package faang.school.postservice.service.post_correct.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import faang.school.postservice.config.post.PostServiceConstants;
import faang.school.postservice.config.webclient.WebSpellHttpConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PostCorrectServiceImplTest {
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

    @InjectMocks
    private PostCorrectServiceImpl postCorrectService;

    @Test
    void testCorrectPostSuccessfully() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Post post = new Post();
        post.setId(1L);
        post.setContent("Posssst");
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

        CompletableFuture<Void> future = postCorrectService.correctPost(post, executorService);
        future.get();
        shutdownExecutor(executorService);

        verify(postRepository).save(any(Post.class));
        assertEquals(correctedContent, post.getContent());
        assertNotNull(post.getUpdatedAt());
    }

    @Test
    void testCorrectPost_NullPost_ThrowsException() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                postCorrectService.correctPost(null, executorService));
        shutdownExecutor(executorService);
        assertEquals("Post or its content cannot be null", exception.getMessage());

    }

    @Test
    void testCheckSpellingWithRetrySuccessfully() throws Exception {
        String content = "Posssst";
        String correctedContent = "Post";

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"corrected\":\"Post\"}");

        JsonNode mockJsonNode = mock(JsonNode.class);
        JsonNode correctedNode = mock(JsonNode.class);
        when(mockJsonNode.path("corrected")).thenReturn(correctedNode);
        when(correctedNode.isMissingNode()).thenReturn(false);
        when(correctedNode.asText()).thenReturn(correctedContent);

        when(webSpellHttpConfig.getWebSpellApiUrl())
                .thenReturn("https://webspellchecker-webspellcheckernet.p.rapidapi.com/api");
        when(webSpellHttpConfig.getWebSpellApiContentType()).thenReturn("application/json");
        when(webSpellHttpConfig.getWebSpellApiKey()).thenReturn("348d2925famsh95c7436e81d45c0p1eeb4ajsn1347b2ce620a");
        when(webSpellHttpConfig.getWebSpellApiHost()).thenReturn("webspellchecker-webspellcheckernet.p.rapidapi.com");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"text\":\"Posssst\"}");
        when(webSpellHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);

        CompletableFuture<String> future = postCorrectService.checkSpellingWithRetry(content);
        String result = future.get();

        assertEquals(correctedContent, result);
    }

    @Test
    void testParseCorrectedContentSuccessfully() throws Exception {
        String responseBody = "{\"corrected\":\"Post\"}";
        String originalContent = "Posssst";
        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(responseBody)).thenReturn(jsonNode);
        when(jsonNode.path("corrected")).thenReturn(jsonNode);
        when(jsonNode.isMissingNode()).thenReturn(false);
        when(jsonNode.asText()).thenReturn("Post");

        String result = postCorrectService.parseCorrectedContent(responseBody, originalContent);

        assertEquals("Post", result);
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(
                    PostServiceConstants.AwaitTermination.EXECUTOR_AWAIT_TERMINATION, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
