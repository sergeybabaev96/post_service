package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Transactional
    public PostDto createDraft(PostDto postDto) {
        postValidator.validatePostDto(postDto);
        Post post = postMapper.toEntity(postDto);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    @Transactional
    public PostDto getPost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostValidationException("Post with id %d does not exist".formatted(postId)));
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostValidationException("Post with id %d does not exist".formatted(postId)));
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(long postId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostValidationException("Post with id %d does not exist".formatted(postId)));
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public void softDeletePost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostValidationException("Post with id %d does not exist".formatted(postId)));
        post.setDeleted(true);
        postRepository.save(post);
    }

}
