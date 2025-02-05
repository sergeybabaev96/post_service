package faang.school.postservice.service;

import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST_NOT_FOUND = "Пост не найден";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    public PostResponseDto create(CreatePostDto createPostDto) {
        postValidator.validateDraftPost(createPostDto);

        Post post = postMapper.toEntity(createPostDto);
        post.setPublished(false);

        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    public PostResponseDto getPost(long postId) {
        Post post = findPostById(postId);
        if (post.isDeleted()) {
            throw new DataNotFoundException(POST_NOT_FOUND);
        }
        return postMapper.toDto(post);
    }

    public PostResponseDto update(long postId, UpdatePostDto updatePostDto) {
        Post post = findPostById(postId);

        post.setContent(updatePostDto.content());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    public PostResponseDto delete(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND + " with id: " + id));
        postValidator.validateNotDeleted(post);
        post.setDeleted(true);
        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(updatedPost);
    }

    public PostResponseDto publish(long id) {
        Post post = findPostById(id);

        postValidator.validateNotPublished(post);
        postValidator.validateNotDeleted(post);
        postValidator.validatePostAuthorExist(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    private Post findPostById(@NotNull long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND));
    }

    private List<PostResponseDto> getPosts(Long id, boolean published, boolean byAuthor) {
        List<Post> posts = byAuthor ? postRepository.findByAuthorId(id) : postRepository.findByProjectId(id);

        posts = posts.stream()
                .filter(post -> post.isPublished() == published && !post.isDeleted())
                .sorted(Comparator.comparing(published ? Post::getPublishedAt : Post::getCreatedAt).reversed())
                .toList();

        return postMapper.toDtoList(posts);
    }

    public List<PostResponseDto> getDraftPostsByAuthorId(long authorId) {
        return getPosts(authorId, false, true);
    }

    public List<PostResponseDto> getDraftPostsByProjectId(long projectId) {
        return getPosts(projectId, false, false);
    }

    public List<PostResponseDto> getPublishedPostsByAuthorId(long authorId) {
        return getPosts(authorId, true, true);
    }

    public List<PostResponseDto> getPublishedPostsByProjectId(long projectId) {
        return getPosts(projectId, true, false);
    }
}