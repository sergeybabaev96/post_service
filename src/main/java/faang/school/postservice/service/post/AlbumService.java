package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.AlbumResponseDto;

import java.util.List;

public interface AlbumService {

    AlbumResponseDto getAlbumById(long id);

    List<AlbumResponseDto> getAlbumsByAuthorId(long authorId);

    AlbumResponseDto changeVisibilityAlbum(long id);
}
