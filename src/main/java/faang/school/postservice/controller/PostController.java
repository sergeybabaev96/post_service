package faang.school.postservice.controller;


import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public PostDto createDraft( @RequestBody PostDto postDto) {
        log.info("Received request to create draft post: {}", postDto);
        PostDto createdDraft = postService.createDraft(postDto);
        log.info("Draft post created successfully with ID: {}", createdDraft.id());
        return createdDraft;
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
    public PostDto updatePost(@PathVariable long postId, @Valid @RequestBody PostDto postDto) {
        return postService.updatePost(postId, postDto.content());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> softDeletePost(@PathVariable long postId) {
        postService.softDeletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/draft/by-author/{authorId}")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable long authorId) {
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/draft/by-project{projectId}")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/by-author/{authorId}")
    public List<PostDto> getAllPostsByAuthorId(@PathVariable long authorId) {
        return postService.getAllPostsByAuthorId(authorId);
    }

    @GetMapping("/by-project/{projectId}")
    public List<PostDto> getAllPostsByProjectId(@PathVariable long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }
}
