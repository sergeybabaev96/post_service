package faang.school.postservice.controller;


import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public ResponseEntity<PostDto> createDraft(@RequestBody PostDto postDto) {
        log.info("Received request to create draft post: {}", postDto);
        PostDto createdDraft = postService.createDraft(postDto);
        log.info("Draft post created successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDraft);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable long postId) {
        log.info("Received request to get post with ID = {}", postId);
        PostDto post = postService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}/publish")
    public ResponseEntity<PostDto> publishPost(@PathVariable long postId) {
        log.info("Received request to publish draft with ID = {}", postId);
        PostDto publishPost = postService.publishPost(postId);
        return ResponseEntity.ok(publishPost);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable long postId, @RequestBody String updatedContent) {
        log.info("Received request to update post with ID = {}", postId);
        PostDto updatedPost = postService.updatePost(postId, updatedContent);
        log.info("Post with ID = {} is updated successfully", postId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> softDeletePost(@PathVariable long postId) {
        log.info("Received request to delete post with ID = {}", postId);
        postService.softDeletePost(postId);
        log.info("post with ID = {} was deleted successfully", postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/draft/by-author/{authorId}")
    public ResponseEntity<List<PostDto>> getAllDraftsByAuthorId(@PathVariable long authorId) {
        log.info("Received request to get all drafts by author with ID = {}", authorId);
        List<PostDto> posts = postService.getAllDraftsByAuthorId(authorId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/draft/by-project/{projectId}")
    public ResponseEntity<List<PostDto>> getAllDraftsByProjectId(@PathVariable long projectId) {
        log.info("Received request to get all drafts by project with ID = {}", projectId);
        List<PostDto> posts = postService.getAllDraftsByProjectId(projectId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<List<PostDto>> getAllPostsByAuthorId(@PathVariable long authorId) {
        log.info("Received request to get all posts by author with ID = {}", authorId);
        List<PostDto> posts =  postService.getAllPostsByAuthorId(authorId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<List<PostDto>> getAllPostsByProjectId(@PathVariable long projectId) {
        log.info("Received request to get all posts by project with ID = {}", projectId);
        List<PostDto> posts =  postService.getAllPostsByProjectId(projectId);
        return ResponseEntity.ok(posts);
    }
}
