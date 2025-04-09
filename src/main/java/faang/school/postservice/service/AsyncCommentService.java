package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncCommentService {

    private final CommentRepository commentRepository;
    private final ModerationDictionaryImpl moderationDictionary;

    @Async("commentModeratorExecutor")
    @Transactional
    public void moderateComments(List<Comment> commentList) {
        List<Comment> moderatedComments = commentList.stream().peek(comment -> {
            String content = comment.getContent();
            if (content == null || content.isBlank()) {
                comment.setVerified(true);
            } else {
                comment.setVerified(moderationDictionary.isTextAreCorrect(content.toLowerCase()));
            }
            comment.setVerifiedAt(LocalDateTime.now());
            log.debug("Comment: {} moderated. Is verified: {}", comment.getId(), comment.getVerified());
        }).toList();

        commentRepository.saveAll(moderatedComments);
    }
}
