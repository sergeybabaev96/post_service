package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.PostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    public AlbumDto createAlbum(long userId, AlbumDto albumDto);

    public AlbumDto addPost(long albumId, long userId, PostDto postDto);

    public List<AlbumDto> showAllAlbums(Optional<AlbumFilterDto> albumFilterDto);

    public Optional<AlbumDto> findById(long albumId);

    public List<AlbumDto> findByAuthorId(long authorId, AlbumFilterDto albumFilterDto);

    public List<PostDto> findByIdWithPosts(long albumId);

    public AlbumDto addAlbumToFavorites(long albumId, long userId);

    public AlbumDto deleteAlbumFromFavorites(long albumId, long userId);

    public List<AlbumDto> findFavoriteAlbumsByUserId(long userId, AlbumFilterDto albumFilterDto);

    public AlbumDto deleteAlbum(long albumId, long userId);
}
