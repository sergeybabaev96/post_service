package faang.school.postservice.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerLikeService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.like_post_event_topic_name}")
    private String likePostEventTopicName;

    public void sendLikePostEvent(Post post, UserDto userDto) {
        LikePostEvent event = new LikePostEvent(post.getAuthorId(), userDto.id(), post.getId());
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("couldn't convert object to json", e);
            throw new RuntimeException("couldn't convert object to json " + e.getMessage());
        }
        kafkaTemplate.send(likePostEventTopicName, json);
    }
}
