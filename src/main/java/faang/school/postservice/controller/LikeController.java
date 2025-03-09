package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/like")
@Slf4j
public class LikeController {
    private LikeService likeService;
    private LikeMapper likeMapper;
    private PostMapper postMapper;
    private UserContext userContext;

    private static final String POST_NEGATIVE_ID = "postId is negative or 0";

    @PostMapping("/post/{postId}/set")
    @ResponseBody
    public LikeDto setLikeToPost(@RequestBody LikeDto likeDto) {
        if (likeDtoIsValidForPost(likeDto)) {
            likeDto.setUserId(userContext.getUserId());
            Like like = likeService.setLikeToPost(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @PostMapping("/post/{postId}/unset")
    @ResponseBody
    public LikeDto unsetLikeToPost(@RequestBody LikeDto likeDto) {
        if (likeDtoIsValidForPost(likeDto)) {
            likeDto.setUserId(userContext.getUserId());
            Like like = likeService.unsetLikeToPost(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/set")
    @ResponseBody
    public LikeDto setLikeToComment(@RequestBody LikeDto likeDto) {
        if (likeDtoIsValidForComment(likeDto)) {
            likeDto.setUserId(userContext.getUserId());
            Like like = likeService.setLikeToComment(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/unset")
    @ResponseBody
    public LikeDto unsetLikeToComment(@RequestBody LikeDto likeDto) {
        if (likeDtoIsValidForPost(likeDto)) {
            likeDto.setUserId(userContext.getUserId());
            Like like = likeService.unsetLikeToComment(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @GetMapping("/post/{postId}")
    @ResponseBody
    public PostDto getNumberOfPostLikes(@PathVariable Long postId) {
        if (idIsValid(postId)) {
        return postMapper.toDto(likeService.getNumberOfPostLikes(postId));
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    private boolean likeDtoIsValidForPost(LikeDto likeDto) {
        return likeDto.getPostId() >= 0;
    }

    private boolean likeDtoIsValidForComment(LikeDto likeDto) {
        return likeDtoIsValidForPost(likeDto)
                && (likeDto.getCommentId() >= 0);
    }

    private boolean idIsValid(Long id) {
        return id >= 0;
    }
}
