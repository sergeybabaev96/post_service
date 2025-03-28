package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("like/post/{postId}")
    public PostDto addLikeToPost(@PathVariable Long postId) {
        return likeService.addLikeToPost(postId);
    }

    @DeleteMapping("/like/posts/{postId}")
    public PostDto removeLikeFromPost(@PathVariable Long postId) {

        return likeService.removeLikeFromPost(postId);
    }

    @PostMapping("/like/comment/{commentId}")
    public CommentDto addLikeToComment(@PathVariable Long commentId) {
        return likeService.addLikeToComment(commentId);
    }

    @DeleteMapping("/like/comments/{commentId}")
    public CommentDto removeLikeFromComment(@PathVariable Long commentId) {
        return likeService.removeLikeFromComment(commentId);
    }
}
