package faang.school.postservice.kafka;

public interface EventPublisher<T> {
    void publish(T message);
}
