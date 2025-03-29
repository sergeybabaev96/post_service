package faang.school.postservice.kafka.producer;

public interface KafkaProducer {

    void produce(String topic, String message);
}