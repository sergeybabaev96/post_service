package faang.school.postservice.controller.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void shouldCreateCommentWhenValidDataProvided() throws Exception {
        Long postId = 1L;
        Long authorId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .content("Test comment")
                .authorId(authorId)
                .postId(postId)
                .build();

        when(commentService.createComment(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated()) // Ожидаем статус 201
                .andExpect(jsonPath("$.content").value("Test comment"))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.postId").value(postId));

        verify(commentService, times(1)).createComment(any(CommentDto.class));
    }

    @Test
    void shouldReturnAllCommentsForPostWhenPostIdIsValid() throws Exception {
        Long postId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Test comment");
        commentDto.setPostId(postId);

        List<CommentDto> comments = Collections.singletonList(commentDto);

        when(commentService.getAllCommentsByPostId(postId)).thenReturn(comments);

        mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test comment"))
                .andExpect(jsonPath("$[0].postId").value(postId));

        verify(commentService, times(1)).getAllCommentsByPostId(postId);
    }

    @Test
    void shouldUpdateCommentWhenValidDataProvided() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;
        Long authorId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .content("Updated comment")
                .authorId(authorId)
                .postId(postId)
                .build();

        when(commentService.updateComment(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value("Updated comment"))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.postId").value(postId));

        verify(commentService, times(1)).updateComment(any(CommentDto.class));
    }

    @Test
    void shouldDeleteCommentWhenValidIdsProvided() throws Exception {
        long commentId = 1L;
        long postId = 1L;

        doNothing().when(commentService).deleteComment(commentId, postId);

        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", 1L, commentId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(commentId, postId);
    }
}