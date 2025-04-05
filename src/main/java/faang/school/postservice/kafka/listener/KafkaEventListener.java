package faang.school.postservice.kafka.listener;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaEventListener {

    void listen(String input, Acknowledgment ack);
}
