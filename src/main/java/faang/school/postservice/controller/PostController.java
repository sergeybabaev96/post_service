package faang.school.postservice.controller;


import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/")
    public PostDto createDraft(@RequestBody @NotNull PostDto postDto) {
        return postService.createDraft(postDto);
    }

}
