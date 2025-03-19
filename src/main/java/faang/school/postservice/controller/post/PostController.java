package faang.school.postservice.controller.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.post.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/post-service")
public class PostController {

    PostService postService;

    @GetMapping("/")
    public PostDto createPost(@RequestBody PostDto postDto) {

        // Произвести первичную валидацию
        // Проверить, что content не пуст

        postService.createPost(postDto);

        return new PostDto();
    }


}
