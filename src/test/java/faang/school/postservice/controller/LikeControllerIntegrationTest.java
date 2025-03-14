package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.TestContainerConfig;
import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.LikeEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "${spring.kafka.topics.like.name}")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LikeControllerIntegrationTest extends TestContainerConfig {
    private static final Logger log = LoggerFactory.getLogger(LikeControllerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private LikeRepository likeRepository;

    @MockBean
    private PostService postService;

    @Qualifier("testLikeKafkaTemplate")
    private KafkaTemplate<String, LikeEvent> kafkaTemplate;

    BlockingQueue<LikeEvent> readEvents = new LinkedBlockingQueue<>();

    @KafkaListener(
            topics = "${spring.kafka.topics.like.name}",
            properties = {"spring.json.value.default.type=faang.school.postservice.model.LikeEvent"},
            containerFactory = "likeEventTestFactory"
    )
    void listenLikeEvents(LikeEvent event) {
        log.info("Read event {}", event);
        readEvents.add(event);
        log.info("Added to queue {}", event);
    }

    @Test
    void likePost_ShouldSendEventToKafka() throws Exception {
        PostLikeDto likeDto = new PostLikeDto(1L, 1L);

        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userServiceClient.getUser(anyLong())).thenReturn(new UserDto(1L, "name", "email"));
        when(postService.getPost(any(Long.class)))
                .thenReturn(Post.builder()
                        .id(1L)
                        .content("content")
                        .build());

        mockMvc.perform(post("/likes/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeDto))
                        .header("x-user-id", 1L))
                .andExpect(status().isOk());
        likeDto.setPostId(2L);
        mockMvc.perform(post("/likes/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeDto))
                        .header("x-user-id", 1L))
                .andExpect(status().isOk());
        assertEquals("name", Objects.requireNonNull(readEvents.poll(30, TimeUnit.SECONDS)).getLikerUsername());
    }
}
