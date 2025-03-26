package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<UserDto>> getAllUsersWhoLikedPost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(likeService.getAllUsersWhoLikedPost(postId));
    }

    @GetMapping("/comments/{commentsId}")
    public ResponseEntity<List<UserDto>> getAllUsersWhoLikedComment(
            @PathVariable Long commentsId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(likeService.getAllUsersWhoLikedComment(commentsId));
    }

    @PostMapping({"/posts/{postId}/likes"})
    public LikeDto likePost(@PathVariable Long postId, @RequestHeader("X-User-Id") Long userId) {
        return likeService.likePost(postId);
    }

    @DeleteMapping({"/posts/{postId}/likes"})
    public LikeDto removeLikeOnPost(@PathVariable Long postId, @RequestHeader("X-User-Id") Long userId) {
        return likeService.removeLikeOnPost(postId);
    }

    @PostMapping({"/comments/{commentId}/likes"})
    public LikeDto likeComment(@PathVariable Long commentId, @RequestHeader("X-User-Id") Long userId) {
        return likeService.likeComment(commentId);
    }

    @DeleteMapping({"/comments/{commentId}/likes"})
    public LikeDto removeLikeOnComment(@PathVariable Long commentId, @RequestHeader("X-User-Id") Long userId) {
        return likeService.removeLikeOnComment(commentId);
    }

    @GetMapping({"/posts/{postId}/countLikes"})
    public PostDto getCountLikesPost(@PathVariable Long postId) {
        return likeService.countLikesPost(postId);
    }
}