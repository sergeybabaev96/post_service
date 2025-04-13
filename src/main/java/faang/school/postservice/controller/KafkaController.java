package faang.school.postservice.controller;

import faang.school.postservice.config.kafka.KafkaProducerConfig;
import faang.school.postservice.dto.user.NotificationDto;
import faang.school.postservice.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducerConfig producer;

    @PostMapping("/sendMessage")
    public String sendNotification(@RequestBody NotificationDto notificationDto) {
        producer.sendNotification(notificationDto);
        return "Уведомление отправлено в Kafka";
    }
}
