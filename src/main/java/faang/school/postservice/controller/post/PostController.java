package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.service.post.implementations.PostServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/post-service")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping("/posts/create/post")
    public PostDto createPostDraft(@RequestBody PostDto postDto) {
        if (postDto.getContent().isBlank()) {
            throw new PostDtoValidationException(
                    "The content of the post must not be empty");
        }
        validateId(postDto.getId());

        return postService.createPostDraft(postDto);
    }

    @PutMapping("/posts/public/post")
    public PostDto publicPost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());

        return postService.publicPost(postDto);
    }

    @PutMapping("/posts/update/post")
    public PostDto updatePost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());

        return postService.updatePost(postDto);
    }

    @DeleteMapping("/posts/delete/post")
    public PostDto deletePost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());

        return postService.deletePost(postDto);
    }

    @GetMapping("/posts/get/post")
    public PostDto getPost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());

        return postService.getPost(postDto);
    }

    @GetMapping(("/posts/get/author_drafts"))
    public List<PostDto> getAuthorPostDrafts(PostDto postDto) {
        /*
        Получение всех черновиков не удаленных постов за авторством пользователя с данным id.
        Посты должны быть отсортированы по дате создания от новых к старым.
        */
        return List.of();
    }

    private void validateId(Long id) {
        if (id < 1) {
            throw new PostDtoValidationException("ID must be greater than zero");
        }
    }
}
