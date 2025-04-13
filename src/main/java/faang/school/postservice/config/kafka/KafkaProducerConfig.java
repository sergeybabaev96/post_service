package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerConfig{

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String topic = "telegram-notification-topic";

    public void sendNotification(NotificationDto dto) {
        try {
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(topic, message);
            log.info("Отправлено сообщение: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации сообщения: {}", e.getMessage(), e);
        }
    }
}
