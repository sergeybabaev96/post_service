package faang.school.postservice.service.album.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.mapper.album.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.album.AlbumRepository;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;
    private final PostMapper postMapper;

    @Override
    public AlbumDto createAlbum(long userId, AlbumDto albumDto) {
        UserDto user = userServiceClient.getUser(userId);
        if (isNull(user)) {
            throw new RuntimeException("User not found");
        }
        if (!isNull(albumRepository.findAlbumByAuthorId((userId)))) {
            throw new RuntimeException("Album already exists");
        }
        Album album = albumMapper.toEntity(albumDto);
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toDto(savedAlbum);
    }

    @Override
    public AlbumDto addPost(long albumId, long userId, PostDto postDto) {
        Album album = albumRepository.findAlbumById(albumId);
        if (isNull(album)) {
            throw new RuntimeException("Album not found");
        }
        if (album.getAuthorId() != userId) {
            throw new RuntimeException("You don't have access to edit this album");
        }
        Post post = postMapper.toEntity(postDto);
        if (album.getPosts().contains(post)) {
            throw new RuntimeException("Post already exists in this album");
        }
        album.getPosts().add(post);
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    @Override
    public List<AlbumDto> showAllAlbums(Optional<AlbumFilterDto> albumFilterDto) {
        return List.of();
    }

    @Override
    public Optional<AlbumDto> findById(long albumId) {
        return Optional.empty();
    }

    @Override
    public List<AlbumDto> findByAuthorId(long authorId, AlbumFilterDto albumFilterDto) {
        return List.of();
    }

    @Override
    public List<PostDto> findByIdWithPosts(long albumId) {
        return List.of();
    }

    @Override
    public AlbumDto addAlbumToFavorites(long albumId, long userId) {
        return null;
    }

    @Override
    public AlbumDto deleteAlbumFromFavorites(long albumId, long userId) {
        return null;
    }

    @Override
    public List<AlbumDto> findFavoriteAlbumsByUserId(long userId, AlbumFilterDto albumFilterDto) {
        return List.of();
    }

    @Override
    public AlbumDto deleteAlbum(long albumId, long userId) {
        return null;
    }
}
