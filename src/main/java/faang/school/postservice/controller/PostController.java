package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    public PostDto createPost(PostDto postDto) {

        return new PostDto();
    }


}
