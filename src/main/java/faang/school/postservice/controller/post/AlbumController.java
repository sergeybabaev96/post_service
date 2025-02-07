package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.service.post.AlbumService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/albums")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/{id}")
    public AlbumResponseDto findAlbumById(@PathVariable @NotNull @Min(0) long id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("/author/{authorId}")
    public List<AlbumResponseDto> findAlbumByAuthorId(@PathVariable @NotNull @Min(0) long authorId) {
        return albumService.getAlbumsByAuthorId(authorId);
    }

    @PutMapping("/{id}/visibility")
    public AlbumResponseDto changeVisibilityAlbum(@PathVariable @NotNull @Min(0) long id) {
        return albumService.changeVisibilityAlbum(id);
    }
}
