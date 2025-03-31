package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping()
    @ResponseBody
    public PostDto createPost(@Validated  @RequestBody CreatePostDto dto) {
        log.info("Creating post, DTO: {}", dto);
        Post post = postMapper.toEntity(dto);
        log.info("Creating post, Entity: {}", post);
        return postMapper.toDto(postService.createPost(post));
    }
}
