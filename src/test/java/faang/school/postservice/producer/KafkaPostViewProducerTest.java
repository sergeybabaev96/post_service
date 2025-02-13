package faang.school.postservice.producer;

import faang.school.postservice.dto.post.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaPostViewProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private NewTopic postViewTopic;

    @InjectMocks
    private KafkaPostViewProducer kafkaPostViewProducer;

    @BeforeEach
    void setUp() {
        when(postViewTopic.name()).thenReturn("postViews");
    }

    @Test
    void sendSuccessTest() {
        PostViewEvent event = new PostViewEvent();
        kafkaPostViewProducer.send(event);
        verify(kafkaTemplate, times(1)).send("postViews", event);
    }
}
