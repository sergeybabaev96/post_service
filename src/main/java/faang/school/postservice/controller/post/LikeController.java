package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.LikeDto;
import faang.school.postservice.service.post.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Лайки")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/likes")
@Validated
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public LikeDto addLikeToPost(@PathVariable @Min(1) @NotNull Long postId) {
        return likeService.addLikeToPost(postId);
    }
}
