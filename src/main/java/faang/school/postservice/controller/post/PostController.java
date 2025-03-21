package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping()
    public PostDto creatingDraftPost(@RequestBody PostDto postDto) {
        validateForCreatePost(postDto);
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/public/{postId}")
    public PostDto publishPost(@PathVariable long postId) {
        validateId(postId, "post");
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @RequestBody PostDto postDto) {
        validateId(postId, "post");
        validateContent(postDto);
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable long postId) {
        validateId(postId, "post");
        postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable long postId) {
        validateId(postId, "post");
        return postService.getPost(postId);
    }

    @GetMapping("/authors/{authorId}/drafts")
    public List<PostDto> getAllAuthorDraftPosts(@PathVariable long authorId) {
        validateId(authorId, "author");
        return postService.getAllAuthorDraftPosts(authorId);
    }
    @GetMapping("/authors/{authorId}/posts")
    public List<PostDto> getAllAuthorPosts(@PathVariable long authorId) {
        validateId(authorId, "author");
        return postService.getAllAuthorPosts(authorId);
    }
    @GetMapping("projects/{projectId}/drafts")
    public List<PostDto> getAllProjectDraftPosts(@PathVariable long projectId) {
        validateId(projectId, "project");
        return postService.getAllProjectDraftPosts(projectId);
    }
    @GetMapping("projects/{projectId}/posts")
    public List<PostDto> getAllProjectPosts(@PathVariable long projectId) {
        validateId(projectId, "project");
        return postService.getAllProjectPosts(projectId);
    }

    private void validateForCreatePost(PostDto postDto) {
        validateId(postDto.getAuthorId(), "author");
        validateId(postDto.getProjectId(), "project");

        validateContent(postDto);
    }

    private static void validateContent(PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new DataValidationException("invalid content: " + postDto.getContent());
        }
    }

    private void validateId(Long id, String field) {
        if (id != null && id < 1) {
            throw new DataValidationException("invalid " + field + " id: " + id);
        }
    }
}
