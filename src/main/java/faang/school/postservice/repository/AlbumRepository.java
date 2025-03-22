package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findAllAlbumsByAuthorId(long authorId);
}
