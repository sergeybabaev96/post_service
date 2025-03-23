package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static faang.school.postservice.utils.ValidationUtils.validateLike;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/like/add")
    public PostDto addLikeToPost(@RequestBody LikeDto likeDto) {
        validateLike(likeDto);
        return likeService.addLikeToPost(likeDto);
    }

    @PostMapping("/post/like/remove")
    public PostDto removeLikeToPost(@RequestBody LikeDto likeDto) {
        validateLike(likeDto);
        return likeService.removeLikeFromPost(likeDto);
    }

    @PostMapping("/comment/like/add")
    public CommentDto addLikeToComment(@RequestBody LikeDto likeDto) {
        validateLike(likeDto);
        return likeService.addLikeToComment(likeDto);
    }

    @PostMapping("/comment/like/remove")
    public CommentDto removeLikeFromComment(@RequestBody LikeDto likeDto) {
        validateLike(likeDto);
        return likeService.removeLikeFromComment(likeDto);
    }
}
