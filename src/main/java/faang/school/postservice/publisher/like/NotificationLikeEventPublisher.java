package faang.school.postservice.publisher.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.event.Event;
import faang.school.event.NotificationLikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationLikeEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final LikeMapper likeMapper;

    @Value("${spring.kafka.topics.notification-like-topic.name}")
    private String notificationLikeTopicName;

    @Override
    public void publishEvent(Object dto) {
        try {
            NotificationLikeEvent event = likeMapper.toNotificationLikeEvent((Like) dto);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(notificationLikeTopicName, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}