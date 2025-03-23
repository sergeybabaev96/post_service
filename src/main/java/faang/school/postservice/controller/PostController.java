package faang.school.postservice.controller;


import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public PostDto createDraft(@RequestBody @Valid PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable long postId) {
        return postService.getPost(postId);
    }

    @PutMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @RequestBody @Valid PostDto postDto) {
        return postService.updatePost(postId, postDto.content());
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> softDeletePost(@PathVariable long postId) {
        postService.softDeletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
