package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostModerationServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private PostModerationService postModerationService;

    private Post cleanPost;
    private Post profanityPost;

    @BeforeEach
    void setUp() {
        cleanPost = new Post();
        cleanPost.setId(1L);
        cleanPost.setContent("This is a clean content");

        profanityPost = new Post();
        profanityPost.setId(2L);
        profanityPost.setContent("This contains bad word");
    }

    @Test
    @DisplayName("Проверка поста, пост должен быть верифицирован")
    public void givenCleanPost_whenCheckForProfanity_thenPostVerified() {
        Set<String> profanityWords = Set.of("bad", "word");
        when(moderationDictionary.getProfanityWord()).thenReturn(profanityWords);

        CompletableFuture<Void> result = postModerationService.checkForProfanity(List.of(cleanPost));

        assertNull(result.join());
        assertTrue(cleanPost.isVerified());
        assertNotNull(cleanPost.getVerifiedAt());

        verify(postRepository).saveAll(List.of(cleanPost));
    }

    @Test
    @DisplayName("Проверка поста, пост не должен быть верифицирован")
    public void givenProfanityPost_whenCheckForProfanity_thenPostNotVerified() {
        Set<String> profanityWords = Set.of("bad", "word");
        when(moderationDictionary.getProfanityWord()).thenReturn(profanityWords);

        CompletableFuture<Void> result = postModerationService.checkForProfanity(List.of(profanityPost));

        assertNull(result.join());
        assertFalse(profanityPost.isVerified());
        assertNotNull(profanityPost.getVerifiedAt());

        verify(postRepository).saveAll(List.of(profanityPost));
    }

    @Test
    @DisplayName("При ошибке сохранения должен выбросить исключение")
    public void givenSaveError_whenCheckForProfanity_thenThrowException() {
        Set<String> profanityWords = Set.of("bad", "word");
        when(moderationDictionary.getProfanityWord()).thenReturn(profanityWords);
        when(postRepository.saveAll(any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class,
                () -> postModerationService.checkForProfanity(List.of(cleanPost)).join());
    }
}