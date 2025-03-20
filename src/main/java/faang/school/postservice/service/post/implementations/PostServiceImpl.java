package faang.school.postservice.service.post.implementations;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.interfaces.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostDto createPostDraft(PostDto postDto) {
        validateDataForCreation(postDto);

        Post post = postRepository.save(postMapper.toEntity(postDto));

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto publicPost(PostDto postDto) {
        Post post = validateDataForPublication(postDto);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = getPostIfExists(postDto.getId());

        post.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDto deletePost(PostDto postDto) {
        Post post = getPostIfExists(postDto.getId());

        post.setDeleted(true);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDto getPost(PostDto postDto) {
        Post post = getPostIfExists(postDto.getId());

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public List<PostDto> getAuthorPostDrafts(PostDto postDto) {
        return List.of();
    }

    @Override
    public List<PostDto> getProjectPostDrafts(PostDto postDto) {
        return List.of();
    }

    @Override
    public List<PostDto> getAuthorPublishedPosts(PostDto postDto) {
        return List.of();
    }

    @Override
    public List<PostDto> getProjectPublishedPosts(PostDto postDto) {
        return List.of();
    }

    /******************************************************************************************************************/
    private Post getPostIfExists(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostDtoValidationException(String.format("Post with ID %d does not exist", id))
        );

        return post;
    }

    private void validatePostExistence(long id) {
        if (postRepository.existsById(id)) {
            throw new PostDtoValidationException(String.format(
                    "Post with ID %d already exists", id));
        }
    }

    private void validateDataForCreation(PostDto postDto) {
        validatePostExistence(postDto.getId());

        if (postDto.getAuthorId() < 0 || postDto.getProjectId() < 0) {
            throw new PostDtoValidationException("ID should not be less than zero!");
        }
        if (postDto.getAuthorId() == 0 && postDto.getProjectId() == 0) {
            throw new PostDtoValidationException("One author required!");
        }
        if (postDto.getAuthorId() != 0 && postDto.getProjectId() != 0) {
            throw new PostDtoValidationException("The author can be either a user or a project!");
        }

        /*if (postDto.getAuthorId() != 0) {
            UserDto userDto = userServiceClient.getUser(postDto.getAuthorId());
            if(userDto.id() == 0) {
                throw new PostDtoValidationException(String.format(
                        "User with ID %d not found!", postDto.getAuthorId()));
            }
        }
        else {
            ProjectDto projectDto = projectServiceClient.getProject(postDto.getProjectId());
            if(projectDto.id() == 0) {
                throw new PostDtoValidationException(String.format(
                        "Project with ID %d not found!", postDto.getProjectId()));
            }
        }*/
    }

    private Post validateDataForPublication(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId()).orElseThrow(() ->
                new PostDtoValidationException(String.format("Post with ID %d does not exist", postDto.getId()))
        );

        if (post.isDeleted()) {
            throw new PostDtoValidationException(String.format(
                    "The post with ID %d removed", postDto.getId()));
        }

        if (post.isPublished()) {
            throw new PostDtoValidationException(String.format(
                    "The post with ID %d has already been published", postDto.getId()));
        }

        return post;
    }
}
