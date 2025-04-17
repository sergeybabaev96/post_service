package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.model.event.EventType;
import faang.school.postservice.properties.RedisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {

    @InjectMocks
    private EventPublisher eventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    private final String topic = "topic";
    private final EventDto event = EventDto.builder().build();

    @BeforeEach
    void setUp() {
        event.setEventType(EventType.PUBLISHED_POST);
    }

    @Test
    void publish_ShouldPublish() {
        when(redisProperties.getTopic(event.getEventType())).thenReturn(topic);

        assertDoesNotThrow(() -> eventPublisher.publish(event));
        verify(redisProperties, times(1)).getTopic(event.getEventType());
        verify(redisTemplate, times(1)).convertAndSend(topic, event);
    }

    @Test
    void publish_ShouldNotPublishWhenEventIsNull() {
        assertThrows(NullPointerException.class, () -> eventPublisher.publish(null));
    }

    @Test
    void publish_ShouldNotPublishWhenEventTypeIsNull() {
        event.setEventType(null);
        when(redisProperties.getTopic(event.getEventType())).thenThrow(NullPointerException.class);
        assertThrows(NullPointerException.class, () -> eventPublisher.publish(event));
    }
}