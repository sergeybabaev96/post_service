package faang.school.postservice.service.post.implementations;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.interfaces.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public PostDto createPostDraft(PostDto postDto) {
        validateDto(postDto);

        Post post = postRepository.save(postMapper.toEntity(postDto));

        return postMapper.toDto(post);
    }

    private void validateDto(PostDto postDto) {
        if(postDto.getAuthorId() == 0 && postDto.getProjectId() == 0) {
            throw new PostDtoValidationException("One author required!");
        }
        if(postDto.getAuthorId() != 0 && postDto.getProjectId() != 0) {
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
        if(postRepository.existsById(postDto.getId())) {
            throw new PostDtoValidationException(String.format(
                    "Post with ID %d already exists", postDto.getId()));
        }
    }

    @Override
    public PostDto publicPost(PostDto postDto) {

        // 1. Пост уже должен существовать
            // Проверить, наличие по id
            // Проверить, что published == false;
            // Проверить, что deleted == false;

        return new PostDto();
    }

    @Override
    public PostDto updatePost(PostDto postDto) {
        return null;
    }

    @Override
    public PostDto deletePost(PostDto postDto) {
        return null;
    }

    @Override
    public PostDto getPost(PostDto postDto) {
        return null;
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


}
