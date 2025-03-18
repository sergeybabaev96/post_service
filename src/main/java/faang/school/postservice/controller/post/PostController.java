package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.service.post.implementations.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/post-service/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPostDraft(@RequestBody PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new PostDtoValidationException(
                    "The content of the post must not be empty");
        }

        return postService.createPostDraft(postDto);
    }

    @PutMapping("/publish")
    public PostDto publishPost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        return postService.publishPost(postDto);
    }

    @PutMapping
    public PostDto updatePost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new PostDtoValidationException(
                    "The content of the post must not be empty");
        }
        return postService.updatePost(postDto);
    }

    @DeleteMapping
    public PostDto deletePost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        return postService.deletePost(postDto);
    }

    @PostMapping("/get")
    public PostDto getPost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        return postService.getPost(postDto);
    }

    @PostMapping("/author/drafts")
    public List<PostDto> getAuthorPostDrafts(@RequestBody PostDto postDto) {
        validateId(postDto.getAuthorId());
        return postService.getAuthorPostDrafts(postDto);
    }

    @PostMapping("/project/drafts")
    public List<PostDto> getProjectPostDrafts(@RequestBody PostDto postDto) {
        validateId(postDto.getProjectId());
        return postService.getProjectPostDrafts(postDto);
    }

    @PostMapping("/author/published")
    public List<PostDto> getAuthorPosts(@RequestBody PostDto postDto) {
        validateId(postDto.getAuthorId());
        return postService.getAuthorPublishedPosts(postDto);
    }

    @PostMapping("/project/published")
    public List<PostDto> getProjectPosts(@RequestBody PostDto postDto) {
        validateId(postDto.getProjectId());
        return postService.getProjectPublishedPosts(postDto);
    }

    private void validateId(long id) {
        if (id < 1) {
            throw new PostDtoValidationException("ID must be greater than zero");
        }
    }
}
