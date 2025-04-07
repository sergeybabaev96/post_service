package faang.school.postservice.producer;

import faang.school.postservice.exception.KafkaMessageSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topicName, T event) {
        sendInternal(
                topicName,
                null,
                event,
                key -> log.info("Event \"{}\" sent successfully to topic \"{}\"", event, topicName)
        );
    }

    public void send(String topicName, String messageKey, T event) {
        sendInternal(
                topicName,
                messageKey,
                event,
                key -> log.info("Event \"{}\" sent successfully to topic \"{}\" with messageKey \"{}\"", event, topicName, key)
        );
    }

    private void sendInternal(String topicName, String messageKey, T event, Consumer<String> successLogger) {
        log.info("Sending Json event: {} to Kafka topic {}", event, topicName);
        try {
            var sendFuture = messageKey != null
                    ? kafkaTemplate.send(topicName, messageKey, event)
                    : kafkaTemplate.send(topicName, event);

            sendFuture.whenComplete((sendResult, exc) -> {
                if (Objects.nonNull(exc)) {
                    log.error("Sending the event \"{}\" to topic \"{}\" failed", event, topicName, exc);
                } else {
                    successLogger.accept(messageKey);
                }
            });
        } catch (Exception e) {
            log.error("An error occurred when sending the event to the topic \"{}\"", topicName);
            throw new KafkaMessageSendingException(
                    "An error occurred when sending the event to the topic \"%s\"".formatted(topicName), e
            );
        }
    }
}
