package faang.school.postservice.util.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.mapper.album.PostMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.album.AlbumRepository;
import faang.school.postservice.service.album.impl.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private AlbumMapperImpl albumMapper;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private AlbumServiceImpl albumServiceimpl;

    UserDto userDto;
    long albumId;
    long userId;
    String title;
    long postId;

    @BeforeEach
    public void setUp() {
        postId = 1L;
        userId = 1L;
        userDto = new UserDto(userId, "test", "test");
        albumId = 1L;
        title = "title";
    }

    @Test
    public void createAlbum_success() {
        AlbumDto dto = AlbumDto.builder()
                .title(title)
                .build();
        Album album = Album.builder()
                .authorId(userId)
                .title(title)
                .build();
        Album savedAlbum = Album.builder()
                .id(albumId)
                .title(title)
                .authorId(userId)
                .build();
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findAlbumByAuthorIdAndTitle(userId, title)).thenReturn(null);
        when(albumRepository.save(album)).thenReturn(savedAlbum);

        AlbumDto albumDto = albumServiceimpl.createAlbum(userId, dto);

        verify(userServiceClient, times(1)).getUser(userId);
        verify(albumRepository, times(1)).findAlbumByAuthorIdAndTitle(userId, title);
        verify(albumMapper, times(1)).toEntity(dto);
        verify(albumRepository, times(1)).save(album);
        verify(albumMapper, times(1)).toDto(savedAlbum);
        dto.setId(1L);
        dto.setAuthorId(userId);
        assertEquals(dto, albumDto);
    }

    @Test
    public void addPost_success() {
        Album album = Album.builder()
                .id(albumId)
                .posts(new ArrayList<>())
                .authorId(userId)
                .build();
        Post post = Post.builder()
                .id(postId)
                .build();
        Album albumWithPost = Album.builder()
                .id(albumId)
                .posts(List.of(post))
                .authorId(userId)
                .build();
        AlbumDto albumWithPostDto = albumMapper.toDto(albumWithPost);
        when(albumRepository.findAlbumById(albumId)).thenReturn(album);
        when(postRepository.findById(postId)).thenReturn(post);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.save(album)).thenReturn(albumWithPost);

        AlbumDto albumDto = albumServiceimpl.addPost(albumId, userId, postId);

        verify(albumRepository, times(3)).findAlbumById(albumId);
        verify(postRepository, times(2)).findById(postId);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(albumRepository, times(1)).save(album);
        verify(albumMapper, times(2)).toDto(albumWithPost);

        assertEquals(albumWithPostDto, albumDto);
    }

    @Test
    public void showAllAlbums_success() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .dateOfCreation(LocalDate.of(2024, 12, 2))
                .title(title)
                .build();
        Optional<AlbumFilterDto> albumFilterDtoOptional = Optional.of(albumFilterDto);
        Album albumOne = Album.builder()
                .id(1L)
                .title(title)
                .createdAt(LocalDateTime.of(2024, 5, 3, 0, 0, 0))
                .build();
        Album albumTwo = Album.builder()
                .id(2L)
                .title("title")
                .createdAt(LocalDateTime.of(2020, 5, 3, 0, 0, 0))
                .build();
        Album albumThree = Album.builder()
                .id(3L)
                .title("another")
                .createdAt(LocalDateTime.of(2021, 5, 3, 0, 0, 0))
                .build();
        List<Album> albums = new ArrayList<>(List.of(albumOne, albumTwo, albumThree));
        AlbumDto albumOneDto = albumMapper.toDto(albumOne);
        when(albumRepository.findAll()).thenReturn(albums);

        List<AlbumDto> albumsDto = albumServiceimpl.showAllAlbums(albumFilterDtoOptional);

        verify(albumRepository, times(1)).findAll();
        verify(albumMapper, times(albumsDto.size() + 1)).toDto(albumOne);

        List<AlbumDto> filteredAlbumDto = new ArrayList<>(List.of(albumOneDto));

        assertEquals(filteredAlbumDto, albumsDto);
    }

    @Test
    public void findById_success() {
        Album album = Album.builder()
                .id(albumId)
                .authorId(1L)
                .build();
        AlbumDto albumDto = albumMapper.toDto(album);
        when(albumRepository.findAlbumById(albumId)).thenReturn(album);

        AlbumDto albumDtoById = albumServiceimpl.findById(albumId);
        verify(albumRepository, times(2)).findAlbumById(albumId);
        verify(albumMapper, times(2)).toDto(album);
        assertEquals(albumDto, albumDtoById);
    }

    @Test
    public void findByAuthorId_success() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .dateOfCreation(LocalDate.of(2024, 12, 2))
                .title(title)
                .build();
        Optional<AlbumFilterDto> albumFilterDtoOptional = Optional.of(albumFilterDto);
        Album albumOne = Album.builder()
                .id(1L)
                .authorId(userId)
                .title(title)
                .createdAt(LocalDateTime.of(2024, 5, 3, 0, 0, 0))
                .build();
        Album albumTwo = Album.builder()
                .id(2L)
                .authorId(userId)
                .title("title")
                .createdAt(LocalDateTime.of(2020, 5, 3, 0, 0, 0))
                .build();
        Album albumThree = Album.builder()
                .id(3L)
                .authorId(userId)
                .title("another")
                .createdAt(LocalDateTime.of(2021, 5, 3, 0, 0, 0))
                .build();
        AlbumDto albumOneDto = albumMapper.toDto(albumOne);
        List<Album> albums = new ArrayList<>(List.of(albumOne, albumTwo, albumThree));
        Optional<List<Album>> optionalAlbums = Optional.of(albums);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findAlbumsByAuthorId(userId)).thenReturn(optionalAlbums);

        List<AlbumDto> albumsDto = albumServiceimpl.findByAuthorId(userId, albumFilterDtoOptional);

        verify(userServiceClient, times(1)).getUser(userId);
        verify(albumRepository, times(1)).findAlbumsByAuthorId(userId);
        verify(albumMapper, times(albumsDto.size() + 1)).toDto(any(Album.class));
        List<AlbumDto> filteredAlbumDto = new ArrayList<>(List.of(albumOneDto));

        assertEquals(filteredAlbumDto, albumsDto);
    }

    @Test
    public void findByIdWithPosts_success() {
        long postOneId = 1L;
        long postTwoId = 2L;
        Post postOne = Post.builder()
                .id(postOneId)
                .build();
        Post postTwo = Post.builder()
                .id(postTwoId)
                .build();
        List<Post> posts = new ArrayList<>(List.of(postOne, postTwo));
        List<PostDto> postsDto = posts.stream().map(postMapper::toDto).collect(Collectors.toList());
        Album album = Album.builder()
                .id(albumId)
                .posts(posts)
                .build();
        when(albumRepository.findAlbumById(albumId)).thenReturn(album);

        List<PostDto> postsList = albumServiceimpl.findByIdWithPosts(albumId);

        verify(albumRepository, times(2)).findAlbumById(albumId);
        verify(postMapper, times(posts.size() * 2)).toDto(any(Post.class));

        assertEquals(postsDto, postsList);
    }

    @Test
    public void addAlbumToFavorites_success() {
        Album album = Album.builder()
                .id(albumId)
                .authorId(userId)
                .build();
        AlbumDto dto = albumMapper.toDto(album);
        when(albumRepository.findAlbumById(albumId)).thenReturn(album);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findAlbumInFavorites(albumId)).thenReturn(null);

        AlbumDto albumDto = albumServiceimpl.addAlbumToFavorites(albumId, userId);

        verify(albumRepository, times(2)).findAlbumById(albumId);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(albumRepository, times(1)).findAlbumInFavorites(albumId);
        verify(albumMapper, times(2)).toDto(album);

        assertEquals(dto, albumDto);
        //особого смысла в этом нет, потому что надо скорее проверить работу репозитория и добавляет ли он альбом в таблицу
    }

    @Test
    public void deleteAlbumFromFavorites_success() {
        Album album = Album.builder()
                .id(albumId)
                .authorId(userId)
                .build();
        AlbumDto dto = albumMapper.toDto(album);
        when(albumRepository.findAlbumById(albumId)).thenReturn(album);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        AlbumDto albumDto = albumServiceimpl.deleteAlbumFromFavorites(albumId, userId);

        verify(albumRepository, times(3)).findAlbumById(albumId);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(albumRepository, times(1)).deleteAlbumFromFavorite(albumId, userId);
        verify(albumMapper, times(2)).toDto(album);

        assertEquals(dto, albumDto);
        //аналогично прошлому тесту
    }

    @Test
    public void findFavoriteAlbumsByUserId() {
        long[] favoriteAlbumsIds = new long[]{1L, 2L, 3L};
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .dateOfCreation(LocalDate.of(2024, 12, 2))
                .title(title)
                .build();
        Optional<AlbumFilterDto> albumFilterDtoOptional = Optional.of(albumFilterDto);
        Album albumOne = Album.builder()
                .id(1L)
                .authorId(userId)
                .title(title)
                .createdAt(LocalDateTime.of(2024, 5, 3, 0, 0, 0))
                .build();
        Album albumTwo = Album.builder()
                .id(2L)
                .authorId(userId)
                .title(title)
                .createdAt(LocalDateTime.of(2020, 5, 3, 0, 0, 0))
                .build();
        Album albumThree = Album.builder()
                .id(3L)
                .authorId(userId)
                .title("another")
                .createdAt(LocalDateTime.of(2021, 5, 3, 0, 0, 0))
                .build();
        AlbumDto albumOneDto = albumMapper.toDto(albumOne);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findFavoriteAlbumIdsByUserId(userId)).thenReturn(favoriteAlbumsIds);
        when(albumRepository.findAlbumById(anyLong())).thenReturn(albumOne, albumTwo, albumThree);

        List<AlbumDto> favoriteAlbumsByUserId = albumServiceimpl.findFavoriteAlbumsByUserId(userId, albumFilterDtoOptional);

        verify(userServiceClient, times(1)).getUser(userId);
        verify(albumRepository, times(1)).findFavoriteAlbumIdsByUserId(userId);
        verify(albumRepository, times(favoriteAlbumsIds.length)).findAlbumById(anyLong());

        assertEquals(List.of(albumOneDto), favoriteAlbumsByUserId);
    }

    @Test
    public void deleteAlbum_success() {
        Album album = Album.builder()
                .id(albumId)
                .authorId(userId)
                .build();
        AlbumDto dto = albumMapper.toDto(album);
        when(albumRepository.findAlbumById(albumId)).thenReturn(album);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        AlbumDto albumDto = albumServiceimpl.deleteAlbum(albumId, userId);

        verify(albumRepository, times(3)).findAlbumById(albumId);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(albumMapper, times(2)).toDto(album);

        assertEquals(dto, albumDto);
    }

    @Test
    public void deletePost_success() {
        List<Post> posts = new ArrayList<>();
        Post post = Post.builder()
                .id(postId)
                .build();
        posts.add(post);
        Album album = Album.builder()
                .id(albumId)
                .authorId(userId)
                .posts(posts)
                .build();
        Album albumWithoutPost = Album.builder()
                .id(albumId)
                .authorId(userId)
                .posts(new ArrayList<>())
                .build();
        when(albumRepository.findAlbumById(albumId)).thenReturn(album);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(post);

        AlbumDto albumDto = albumServiceimpl.deletePost(albumId, userId, postId);

        AlbumDto dto = albumMapper.toDto(albumWithoutPost);
        verify(albumRepository, times(3)).findAlbumById(albumId);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(postRepository, times(2)).findById(postId);

        assertEquals(dto, albumDto);
    }
}
