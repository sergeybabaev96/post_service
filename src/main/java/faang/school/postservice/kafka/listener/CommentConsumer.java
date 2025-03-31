package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.Post.PostCacheDto;
import faang.school.postservice.dto.comment.CommentForListDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.CommentEvent;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentConsumer {
    private final RedisPostRepository redisPostRepository;
    private final CommentMapper commentMapper;

    @Value("${cache.max-comments:3}")
    private int maxComments;

    @KafkaListener(topics = "${spring.kafka.topics.comment.name}",
            containerFactory = "commentContainerFactory")
    public void commentEventListener(CommentEvent commentEvent, Acknowledgment ack) {
        log.info("Get event {}", commentEvent);
        Long postId = commentEvent.getPostId();
        CommentForListDto comment = commentMapper.toListDto(commentEvent);

        Optional<PostCacheDto> post = redisPostRepository.findById(postId);

        post.ifPresent(p -> {
            log.info("Post {} has {} comments", p.getId(), p.getComments());
            Set<CommentForListDto> comments = p.getComments();
            if (comments == null) {
                comments = new LinkedHashSet<>();
            }
            if (comments.size() >= maxComments) {
                comments.remove(comments.iterator().next());
            }
            comments.add(comment);
            p.setComments(comments);
            log.info("Post {} has {}", p.getId(), p.getComments());
            redisPostRepository.save(p);
        });

        ack.acknowledge();
    }
}
