package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.model.AlbumVisibility;

import java.util.List;

public interface AlbumService {

    AlbumResponseDto getAlbumById(long id);

    List<AlbumResponseDto> getAlbumsByAuthorId(long authorId);

    void updateAlbumVisibility(long id, AlbumVisibility albumVisibility);

    void addUsersForAccessAlbum(long id, AlbumUsersDto albumUsersDto);
}
