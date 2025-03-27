package faang.school.postservice.repository.album;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    boolean findAlbumByAuthorId(long authorId);

    Album findAlbumById(long id);

}
