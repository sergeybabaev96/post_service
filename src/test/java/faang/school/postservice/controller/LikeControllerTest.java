package faang.school.postservice.controller;


import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLikeForPost() {

        LikePostDto likePostDto = new LikePostDto(100L, 1L);
        LikePostDtoResponse expectedResponse = new LikePostDtoResponse(1L, 100L, null);

        when(likeService.createLikeForPost(likePostDto)).thenReturn(expectedResponse);
        LikePostDtoResponse actualResponse = likeController.likeForPost(likePostDto);

        assertEquals(expectedResponse, actualResponse);
        verify(likeService, times(1)).createLikeForPost(likePostDto);
    }

    @Test
    void testLikeForComment() {

        LikeCommentDto likeCommentDto = new LikeCommentDto(100L, 1L, 10L);
        LikeCommentDtoResponse expectedResponse = new LikeCommentDtoResponse(1L, 100L, 1L, null);

        when(likeService.createLikeForComment(likeCommentDto)).thenReturn(expectedResponse);
        LikeCommentDtoResponse actualResponse = likeController.likeForComment(likeCommentDto);

        assertEquals(expectedResponse, actualResponse);
        verify(likeService, times(1)).createLikeForComment(likeCommentDto);
    }

    @Test
    void testDeleteLikeFromPost() {

        Long postId = 1L;
        likeController.deleteLikeFromPost(postId);
        verify(likeService, times(1)).deleteLikeFromPost(postId);
    }

    @Test
    void testDeleteLikeFromComment() {

        Long commentId = 10L;
        likeController.deleteLikeFromComment(commentId);

        verify(likeService, times(1)).deleteLikeFromComment(commentId);
    }
}
