package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.CustomKafkaProperties;
import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.user.UserDtoAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostEventProducer extends KafkaProducerService{

    private final CustomKafkaProperties customKafkaProperties;

    private final UserDtoAdapter userDtoAdapter;

    public PostEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                             CustomKafkaProperties customKafkaProperties,
                             ObjectMapper objectMapper,
                             UserDtoAdapter userDtoAdapter) {
        super(kafkaTemplate, objectMapper);
        this.customKafkaProperties = customKafkaProperties;
        this.userDtoAdapter = userDtoAdapter;
    }

    @Async("asyncTaskExecutor")
    public void producePublishPostEventAsync(long postId, List<Long> followersIds) {
        producePublishPostEvent(postId, followersIds);
    }

    public void producePublishPostEvent(long postId, List<Long> followersIds) {
        //Long userId = post.getAuthorId();

        PostPublicationEvent postPublicationEvent = PostPublicationEvent.builder()
                //.userId(userId)
                .postId(postId)
                .followersIds(followersIds)
                .build();
        super.sendPostMessage(customKafkaProperties.topic().postsTopic(), postPublicationEvent);
        log.info("Sending PublishPostEvent to message broker. Post : {}", postId);
    }

    public void produceViewPostEvent(long postId, Long visitorId) {

        PostViewEvent postViewEvent = PostViewEvent.builder()
                .postId(postId)
                .userId(visitorId)
                .build();

        super.sendPostMessage(customKafkaProperties.topic().postViewsTopic(), postViewEvent);
        log.info("Sending PostViewEvent to message broker. Post : {}", postId);
    }




    /*
    private void sendPostMessage(String topic, PostPublicationEvent post) {
        try {
            sendMessage(topic, super.objectMapper.writeValueAsString(post));
        } catch (JsonProcessingException e) {
            log.error("Error serializing post message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/
}
