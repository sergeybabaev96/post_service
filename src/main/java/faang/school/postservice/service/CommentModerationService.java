package faang.school.postservice.service;

import faang.school.postservice.exception.ModerationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Сервис для модерации комментариев пользователей.
 * <p>
 * Проверяет наличие запрещённых слов в комментариях и проставляет флаги верификации.
 * Верификация включает установку времени проверки и флага {@code verified}, который зависит от результата фильтрации.
 * Если будет найдено запрещённое слово — комментарий считается непрошедшим модерацию.
 * <p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentModerationService {
    private final CommentRepository commentRepository;

    /**
     * Метод для модерации комментариев.
     * <p>
     * Проверяет наличие запрещённых слов в комментариях и проставляет флаги верификации.
     * Верификация включает установку времени проверки и флага {@code verified},
     * который зависит от результата фильтрации.
     * Если будет найдено запрещённое слово — комментарий считается непрошедшим модерацию.
     *
     * @param comments       список комментариев для модерации
     * @param profanityWords множество запрещённых слов
     */
    @Transactional
    public void moderateComments(@NotNull List<Comment> comments, Set<String> profanityWords) {
        if (comments == null) {
            log.error("Comments list cannot be null");
            throw new ModerationException("Comments list cannot be null");
        }

        if (comments.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        comments.forEach(comment -> {
            String content = comment.getContent().toLowerCase();
            boolean profanityFound = profanityWords.stream()
                    .anyMatch(content::contains);

            comment.setVerifiedAt(now);
            comment.setVerified(!profanityFound);
        });

        commentRepository.saveAll(comments);
    }
}