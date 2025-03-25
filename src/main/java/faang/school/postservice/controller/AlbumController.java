package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/{id}")
    public AlbumResponseDto findAlbumById(@PathVariable @NotNull @Min(1) long id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("/author/{authorId}")
    public List<AlbumResponseDto> findAlbumsByAuthorId(@PathVariable @NotNull @Min(1) long authorId) {
        return albumService.getAlbumsByAuthorId(authorId);
    }

    @PutMapping("/{id}/visibility/{visibility}")
    public void updateAlbumVisibility(@PathVariable @NotNull @Min(1) long id,
                                      @PathVariable("visibility") @NotNull AlbumVisibility visibility) {
        albumService.updateAlbumVisibility(id, visibility);
    }

    @PutMapping("/{id}/add-users-for-access")
    public void addUsersForAccessAlbum(@PathVariable @NotNull @Min(1) long id,
                                       @RequestBody @Valid AlbumUsersDto albumUsersDto) {
        albumService.addUsersForAccessAlbum(id, albumUsersDto);
    }
}
