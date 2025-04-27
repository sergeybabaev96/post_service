package faang.school.postservice.controller;

import faang.school.postservice.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.simpleframework.xml.Path;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/images")
@RequiredArgsConstructor
public class PostImageController {

    private final PostImageService postImageService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<String> addingImagesToAPost(@PathVariable Long postId,
                                                      @RequestPart("files") List<MultipartFile> images) {
        postImageService.addingImagesToAPost(postId, images);
        return ResponseEntity.ok()
                .body("Uploading images: " + images.size());
    }

    @DeleteMapping("/{resourceId}/post/{postId}")
    public ResponseEntity<String> deleteImageFromAPost(@PathVariable Long postId, @PathVariable Long resourceId) {
        postImageService.deleteImageFromAPost(postId, resourceId);
        return ResponseEntity.ok()
                .body("Deleting image " + resourceId + " successfully ended");
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable Long resourceId) {
        InputStream stream = postImageService.getImage(resourceId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(stream));
    }
}
