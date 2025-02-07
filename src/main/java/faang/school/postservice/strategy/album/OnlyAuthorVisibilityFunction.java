package faang.school.postservice.strategy.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OnlyAuthorVisibilityFunction implements Function<Album, AlbumResponseDto> {

    private final AlbumMapper albumMapper;
    private final UserContext userContext;

    @Override
    public AlbumResponseDto apply(Album album) {
        long userId = userContext.getUserId();
        checkUserIsAuthor(userId, album);
        return albumMapper.toDto(album);
    }

    private void checkUserIsAuthor(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            throw new AlbumAccessDeniedException(
                    String.format("Access denied for user with id = %d for album with id = %d", userId, album.getId()));
        }
    }
}
