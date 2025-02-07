package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.post.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final Map<Visibility, Function<Album, AlbumResponseDto>> visibilities;

    @Override
    public AlbumResponseDto getAlbumById(long id) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        return visibilities.get(album.getVisibility()).apply(album);
    }

    @Override
    public List<AlbumResponseDto> getAlbumsByAuthorId(long authorId) {
        return albumRepository.findByAuthorId(authorId).stream()
                .map(album -> visibilities.get(album.getVisibility()).apply(album))
                .toList();
    }

    @Override
    public AlbumResponseDto changeVisibilityAlbum(long id) {
        return null;
    }
}
