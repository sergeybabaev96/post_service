package faang.school.postservice.controller;

import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    private final Long firstId = 1L;

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    public void testPositivePutLikeOnPost() throws Exception {
        likeController.putLikeOnPost(firstId, firstId);

        verify(likeService, times(1)).putLikeOnPost(firstId, firstId);

        mockMvc.perform(post("/likes/post-{postId}", firstId)
                .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositiveRemoveLikeOnPost() throws Exception {
        likeController.removeLikeAtPost(firstId, firstId);

        verify(likeService, times(1)).removeLikeAtPost(firstId, firstId);

        mockMvc.perform(delete("/likes/post-{postId}", firstId)
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositivePutLikeOnComment() throws Exception {
        likeController.putLikeOnComment(firstId, firstId);

        verify(likeService, times(1)).putLikeOnComment(firstId, firstId);

        mockMvc.perform(post("/likes/comment-{commentId}", firstId)
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositiveRemoveLikeOnComment() throws Exception {
        likeController.removeLikeAtComment(firstId, firstId);

        verify(likeService, times(1)).removeLikeAtComment(firstId, firstId);

        mockMvc.perform(delete("/likes/comment-{commentId}", firstId)
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }


}
