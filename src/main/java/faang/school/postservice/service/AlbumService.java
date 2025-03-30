package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.PostAlbumDto;

public interface AlbumService {
    AlbumDto create(AlbumDto dto);

    boolean existsById(long l);
}
