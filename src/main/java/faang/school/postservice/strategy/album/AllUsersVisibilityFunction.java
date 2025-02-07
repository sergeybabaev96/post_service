package faang.school.postservice.strategy.album;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AllUsersVisibilityFunction implements Function<Album, AlbumResponseDto> {

    private final AlbumMapper albumMapper;

    @Override
    public AlbumResponseDto apply(Album album) {
        return albumMapper.toDto(album);
    }
}
