package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для модерации постов на наличие нецензурной лексики.
 * <p>
 * Обеспечивает асинхронную проверку постов с использованием словаря запрещенных слов.
 * Каждый пост проходит верификацию и помечается соответствующим статусом.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Пакетная проверка постов на запрещенную лексику</li>
 *   <li>Автоматическая пометка статуса верификации</li>
 *   <li>Фиксация времени проверки</li>
 *   <li>Асинхронная обработка с транзакционной поддержкой</li>
 * </ul>
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostModerationService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    /**
     * Проверяет пакет постов на наличие нецензурной лексики.
     * <p>
     * Выполняется асинхронно в отдельной транзакции. Для каждого поста:
     * <ol>
     *   <li>Проверяет содержание на наличие слов из словаря</li>
     *   <li>Устанавливает дату верификации</li>
     *   <li>Помечает пост как верифицированный/не прошедший проверку</li>
     * </ol>
     *
     * @param posts пакет постов для проверки
     * @return CompletableFuture<Void> для отслеживания завершения операции
     * @throws RuntimeException если произошла ошибка при сохранении
     */
    @Async
    @Transactional
    public CompletableFuture<Void> checkForProfanity(@NotNull List<Post> posts) {
        try {
            Set<String> profanity = moderationDictionary.getProfanityWord();
            LocalDateTime now = LocalDateTime.now();

            posts.forEach(post -> {
                String content = post.getContent().toLowerCase();
                boolean profanityFound = profanity.stream()
                        .anyMatch(content::contains);

                post.setVerifiedAt(now);
                post.setVerified(!profanityFound);
            });

            postRepository.saveAll(posts);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error processing batch of posts: {}", e.getMessage(), e);
            throw new RuntimeException("Batch processing failed", e);
        }
    }
}