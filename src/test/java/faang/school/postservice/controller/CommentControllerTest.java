package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.context.UserHeaderFilter;
import faang.school.postservice.dto.comment.CommentDto;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ContextConfiguration(classes = {UserContext.class, CommentService.class, CommentController.class})
@WebMvcTest
@AutoConfigureMockMvc
@Slf4j
public class CommentControllerTest {

    private CommentDto commentDto;

    private final String urlCreate = "/comments/create";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CommentService service;

    @MockBean
    private UserHeaderFilter userHeaderFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentMapper mapper;

    @BeforeEach
    public void setUp() {
        commentDto = CommentDto.builder()
                .id(1)
                .authorId(1)
                .postId(1)
                .content("content")
                .build();
    }

    @Test
    void positiveCreateComment() throws Exception {
        CommentDto inputDto = new CommentDto(1L, 1L, 1L, "Test", LocalDateTime.now());
        CommentDto outputDto = new CommentDto(1L, 1L, 1L, "Test", LocalDateTime.now());

        when(service.createComment(1L, 1L, inputDto)).thenReturn(outputDto);

        mockMvc.perform(post("/comments/create")
                        .param("userId", "1")
                        .param("postId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void editCommentValidRequestReturnsUpdatedComment() throws Exception {
        // Подготовка данных
        long commentId = 1L;
        String newContent = "Updated content";
        CommentDto inputDto = new CommentDto(commentId, 1L, 1L, "Old content", null);
        CommentDto outputDto = new CommentDto(commentId, 1L, 1L, newContent, LocalDateTime.now());

        when(service.editComment(any(CommentDto.class), eq(commentId), eq(newContent)))
                .thenReturn(outputDto);

        mockMvc.perform(put("/comments/edit/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .param("content", newContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value(newContent));
    }

    @Test
    void getAllCommentsValidPostIdReturnsComments() throws Exception {

        Long postId = 123L;
        CommentDto comment = new CommentDto(
                1L,
                999L,
                postId,
                "Test comment",
                LocalDateTime.now()
        );

        when(service.getAllComments(postId)).thenReturn(List.of(comment));
        
        mockMvc.perform(get("/comments/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test comment"));
    }

    @Test
    void testDeleteComment_Simple() throws Exception {
        mockMvc.perform(delete("/comments/delete/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }






}
