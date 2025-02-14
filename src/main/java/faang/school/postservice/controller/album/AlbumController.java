package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumEditDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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

@RequestMapping("/albums")
@RestController
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public AlbumReadDto createAlbum(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        return albumService.createAlbum(albumCreateDto);
    }

    @PostMapping("/favorite/add/{albumId}")
    public AlbumReadDto addAlbumToFavorite(@Valid @Positive @PathVariable long albumId) {
        return albumService.addAlbumToFavorites(albumId);
    }

    @PostMapping("/favorite/delete/{albumId}")
    public AlbumReadDto deleteAlbumFromFavorite(@Valid @Positive @PathVariable long albumId) {
        return albumService.deleteAlbumFromFavorites(albumId);
    }

    @GetMapping("/{albumId}")
    public AlbumReadDto findAlbumById(@Valid @Positive @PathVariable long albumId) {
        return albumService.findAlbumById(albumId);
    }

    @GetMapping("/filters/{authorId}")
    public List<AlbumReadDto> findAuthorAlbumsByFilters(@Valid @RequestBody AlbumFilterDto filterDto, @Valid @Positive @PathVariable long authorId) {
        return albumService.findAuthorAlbumsByFilters(filterDto, authorId);
    }

    @GetMapping("/filters")
    public List<AlbumReadDto> findAllAlbumsByFilters(@Valid @RequestBody AlbumFilterDto filterDto) {
        return albumService.findAllAlbumsByFilters(filterDto);
    }

    @GetMapping("/favorite/filters")
    public List<AlbumReadDto> findFavoriteAlbumsByFilters(@Valid @RequestBody AlbumFilterDto filterDto) {
        return albumService.findFavoriteAlbumsByFilters(filterDto);
    }

    @PutMapping
    public AlbumReadDto editAlbum(@Valid @RequestBody AlbumEditDto albumEditDto) {
        return albumService.editAlbum(albumEditDto);
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@Valid @Positive @PathVariable long albumId) {
        albumService.deleteAlbum(albumId);
    }
}
