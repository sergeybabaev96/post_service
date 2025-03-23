package faang.school.postservice.validation;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private UserServiceClient userServiceClient;
    private ProjectServiceClient projectServiceClient;
    private PostRepository postRepository;

    public void validatePostDto(PostDto postDto) {
        if (postDto == null) {
            throw new PostValidationException("postDto cannot be null");
        }

        if (postDto.content() == null || postDto.content().isBlank()) {
            throw new PostValidationException("Post content cannot be empty");
        }

        if (postDto.authorId() == null) {
            throw new PostValidationException("author ID cannot be null");
        }

        UserDto userDto = userServiceClient.getUser(postDto.authorId());
        if (userDto == null) {
            throw new PostValidationException("author ID does not exist");
        }

        if (postDto.projectId() != null) {
            ProjectDto projectDto = projectServiceClient.getProject(postDto.projectId());
            if (projectDto == null) {
                throw new PostValidationException("project ID does not exist");
            }
        }
    }

    public void validateDraft(long postId) {
         Post post = postRepository.findById(postId).orElseThrow(() ->
                 new PostValidationException("Draft with id %d does not exist".formatted(postId)));
        if (post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }
        if (!post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }
    }

    public void validatePostExistence(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostValidationException("Draft with id %d does not exist".formatted(postId)));
    }
}
