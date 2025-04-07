package faang.school.postservice.repository.album;

import faang.school.postservice.model.Album;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    @Query(value = "SELECT a FROM Album a WHERE a.authorId = :authorId AND a.title = :title")
    List<Album> findAlbumByAuthorIdAndTitle(long authorId, String title);

    @Query(value = "SELECT a FROM Album a WHERE a.authorId = :authorId")
    Optional<List<Album>> findAlbumsByAuthorId(long authorId);

    @Query(value = "SELECT a FROM Album a WHERE a.id = :id")
    Album findAlbumById(long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO favorite_albums (album_id, user_id) VALUES (:albumId, :userId)")
    void addAlbumToFavorite(long albumId, long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM favorite_albums WHERE user_id = :userId AND album_id = :albumId")
    void deleteAlbumFromFavorite(long albumId, long userId);

    @Query(nativeQuery = true, value = "SELECT album_id FROM favorite_albums WHERE user_id = :userId")
    long[] findFavoriteAlbumIdsByUserId(long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM favorite_albums WHERE album_id = :albumId")
    Album findAlbumInFavorites(long albumId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Album a WHERE a.id = :id")
    void deleteAlbumById(long id);
}
