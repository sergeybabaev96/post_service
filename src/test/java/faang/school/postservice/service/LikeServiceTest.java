package faang.school.postservice.service;

import faang.school.postservice.dto.LikeViewDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.LikeValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private PostService postService;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private LikeService likeService;

    private Like likeEntity;
    private Post postEntity;
    private LikeViewDto likeViewDto;
    private Comment commentEntity;
    private long postId;
    private long commentId;
    private long userId;

    @BeforeEach
    void setUp() {
        postId = 1L;
        userId = 1L;
        commentId = 1L;

        likeEntity = new Like();

        postEntity = new Post();

        commentEntity = new Comment();

        likeViewDto = new LikeViewDto();
        likeViewDto.setUserId(userId);
    }

    @Test
    @DisplayName("likePost: позитивный сценарий")
    void givenValidPostAndUserWhenLikePostThenReturnLikeViewDto() {
        likeViewDto.setPostId(postId);
        Mockito.when(postService.getPostEntity(postId)).thenReturn(postEntity);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(likeEntity);
        Mockito.when(likeMapper.toDto(likeEntity)).thenReturn(likeViewDto);

        LikeViewDto result = likeService.likePost(postId, userId);

        Assertions.assertEquals(likeViewDto, result);

        Mockito.verify(likeValidator).validatePostLikeConditions(postId, userId);
        Mockito.verify(postService).getPostEntity(postId);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(likeMapper).toDto(likeEntity);
    }

    @Test
    @DisplayName("unlikePost: позитивный сценарий")
    public void givenValidPostAndUserWhenUnlikePostThenLikeIsDeleted() {
        likeService.unlikePost(postId, userId);
        Mockito.verify(likeValidator, Mockito.times(1))
                .validatePostUnlikeConditions(postId, userId);
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("likeComment: позитивный сценарий")
    void givenValidPostAndUserWhenLikeCommentThenReturnLikeViewDto() {
        likeViewDto.setCommentId(commentId);
        Mockito.when(commentService.getComment(commentId)).thenReturn(commentEntity);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(likeEntity);
        Mockito.when(likeMapper.toDto(likeEntity)).thenReturn(likeViewDto);

        LikeViewDto result = likeService.likeComment(commentId, userId);

        Assertions.assertEquals(likeViewDto, result);

        Mockito.verify(likeValidator).validateCommentLikeConditions(commentId, userId);
        Mockito.verify(commentService).getComment(commentId);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(likeMapper).toDto(likeEntity);
    }

    @Test
    @DisplayName("unlikeComment: позитивный сценарий")
    public void givenValidPostAndUserWhenUnlikeCommentThenLikeIsDeleted() {
        likeService.unlikeComment(commentId, userId);
        Mockito.verify(likeValidator, Mockito.times(1))
                .validateCommentUnlikeConditions(commentId, userId);
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByCommentIdAndUserId(commentId, userId);
    }
}
