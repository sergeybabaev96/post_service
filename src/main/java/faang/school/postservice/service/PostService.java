package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.ad.AdRepository;
import feign.FeignException;
import liquibase.hub.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final ResourceRepository resourceRepository;
    private final AlbumRepository albumRepository;

    public PostDto create(PostDto postDto) {
        validateContent(postDto);
        validateAuthor(postDto.authorId(), postDto.projectId());
        Post post = postMapper.toEntity(postDto);
        Ad ad = adRepository.findById(postDto.adId()).orElseThrow(
                () -> new RuntimeException("ad not found"));
        List<Comment> comments = commentRepository.findByIdIn(postDto.commentsId());
        List<Like> likes = likeRepository.findByIdIn(postDto.likesId());
        List<Resource> resources = resourceRepository.findByIdIn(postDto.resourcesId());
        List<Album> albums = albumRepository.findByIdIn(postDto.albumsId());

        post.setAd(ad);
        post.setComments(comments);
        post.setLikes(likes);
        post.setResources(resources);
        post.setAlbums(albums);

        postRepository.save(post);
        return postMapper.toDto(post);
    }

    private void validateContent(PostDto postDto) {
        if (postDto == null) {
            throw new IllegalArgumentException("postDto is null");
        }
        if (postDto.content() == null || postDto.content().isBlank()) {
            throw new NullPointerException("Content is null or empty");
        }
    }

    private void validateAuthor(Long authorId, Long projectId) {
        boolean isProject = projectId != null;
        boolean isUser = authorId != null;

        // Проверка, что указан ровно один автор
        if (isProject && isUser) {
            throw new IllegalArgumentException("Должен быть указан только один автор: либо пользователь, либо проект.");
        }

        // Проверка существования проекта, если указан projectId
        if (isProject) {
            if (!existsProject(projectId)) {
                throw new RuntimeException("Проект с ID " + projectId + " не существует.");
            }
        }

        // Проверка существования пользователя, если указан authorId
        if (isUser) {
            if (!existsUser(authorId)) {
                throw new RuntimeException("Пользователь с ID " + authorId + " не существует.");
            }
        }
    }

    // Обновленные методы проверки существования пользователя и проекта
    private boolean existsUser(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
            return true; // Если пользователь найден, возвращаем true
        } catch (FeignException e) {
            return false; // Если исключение, значит, пользователь не существует
        }
    }

    private boolean existsProject(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
            return true; // Если проект найден, возвращаем true
        } catch (FeignException e) {
            return false; // Если исключение, значит, проект не существует
        }
    }
}


