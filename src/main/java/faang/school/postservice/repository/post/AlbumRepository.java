package faang.school.postservice.repository.post;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByAuthorId(long id);

    @Query(nativeQuery = true, value = "SELECT user_id FROM allowed_users_album WHERE album_id = :id")
    List<Long> findSelectedUsersForAlbum(long id);

}
