package faang.school.postservice.controller;

import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post-{postId}")
    public void putLikeOnPost(@PathVariable Long postId, @RequestParam Long userId) {
        likeService.putLikeOnPost(postId, userId);
    }

    @DeleteMapping("/post-{postId}")
    public void removeLikeAtPost(@PathVariable Long postId, @RequestParam Long userId) {
        likeService.removeLikeAtPost(postId, userId);
    }

    @PostMapping("/comment-{commentId}")
    public void putLikeOnComment(@PathVariable Long commentId, @RequestParam Long userId) {
        likeService.putLikeOnComment(commentId, userId);
    }

    @DeleteMapping("/comment-{commentId}")
    public void removeLikeAtComment(@PathVariable Long commentId, @RequestParam Long userId) {
        likeService.removeLikeAtComment(commentId, userId);
    }
}
