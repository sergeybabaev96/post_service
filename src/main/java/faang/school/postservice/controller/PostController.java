package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PutMapping("/{postId}")
    public @Validated List<ResourceDto> addResource(@PathVariable Long postId,
                                              @RequestParam("files") List<MultipartFile> files) {
        List<ResourceDto> resourceDtoList = postService.add(postId, files);
        return resourceDtoList;
    }

    @DeleteMapping("/{postId}/resources/{resourceId}")
    public void deleteResource(@PathVariable Long postId, @PathVariable Long resourceId) {
        postService.delete(postId, resourceId);
    }
}
