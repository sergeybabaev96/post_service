package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentCreateEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private static final String COMMENT_CREATE_TOPIC = "comment_create";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCommentCreateMessage(Long ownerPostUserId, Long userIdCommentCreated, Long postId) {
        CommentCreateEventDto commentCreateEventDto = new CommentCreateEventDto(
                ownerPostUserId,
                postId,
                userIdCommentCreated
        );

        try {
            String message = objectMapper.writeValueAsString(commentCreateEventDto);
            kafkaTemplate.send(COMMENT_CREATE_TOPIC, commentCreateEventDto.toString());
            log.info("Sent comment create message {} to topic: {}", message, COMMENT_CREATE_TOPIC);
        } catch (Exception e) {
            log.error("Error while sending comment create message {}", e.getMessage());
            throw new RuntimeException("Error while sending comment create message");
        }
    }
}
