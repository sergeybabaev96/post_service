package faang.school.postservice.publisher.like;

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

    private final KafkaTemplate<String, Event> kafkaTemplate;
    private final LikeMapper likeMapper;

    @Value("${spring.kafka.topics.notification-like-topic.name}")
    private String notificationLikeTopicName;

    @Override
    public void publishEvent(Object dto) {
        NotificationLikeEvent event = likeMapper.toNotificationLikeEvent((Like) dto);
        kafkaTemplate.send(notificationLikeTopicName, event);
    }
}