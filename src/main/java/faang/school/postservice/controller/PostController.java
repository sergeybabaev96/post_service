package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponseDto create(@Valid @NotNull @RequestBody CreatePostDto createPostDto) {
        return postService.create(createPostDto);
    }

    @PutMapping("/{postId}")
    public PostResponseDto update(@PathVariable long postId, @Valid @NotNull @RequestBody UpdatePostDto updatePostDto) {
        return postService.update(postId, updatePostDto);
    }

    @DeleteMapping("/{postId}")
    public PostResponseDto delete(@PathVariable long postId) {
        return postService.delete(postId);
    }

    @PostMapping("/{postId}/publishing")
    public PostResponseDto publishPost(@PathVariable long postId) {
        return postService.publish(postId);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/filter")
    public List<PostResponseDto> getFilteredPosts(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long projectId,
            @RequestParam Boolean isPublished) {

        FilterDto filterDto = new FilterDto(authorId, projectId, isPublished);
        return postService.getFilteredPosts(filterDto);
    }
}