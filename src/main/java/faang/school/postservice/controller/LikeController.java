package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/users/post/{postId}")
    public List<UserDto> getPostLikeUsers(@NonNull @PathVariable("postId") Long postId) {
        return likeService.getPostLikes(postId);
    }

    @GetMapping("/users/comment/{commentId}")
    public List<UserDto> getCommentLikeUsers(@NonNull @PathVariable("commentId") Long commentId) {
        return likeService.getCommentLikes(commentId);
    }
}
