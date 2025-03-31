package faang.school.postservice.validator.creatpost;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CreatePostValidator {

    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private ProjectServiceClient projectServiceClient;

    public void validateIsPostCreator(Post post) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();
        log.info("AuthorId: {}, projectId: {}", authorId, projectId);

        if (authorId != null) {
            log.info("getting user");
            UserDto user = userServiceClient.getUser(authorId);
            log.info("User: {}", user);
            if (user == null) {
                throw new EntityNotFoundException("User with id = " + authorId + " not found");
            }

            return;
        } else if (projectId != null) {
            ProjectDto project = projectServiceClient.getProject(projectId);
            if (project == null) {
                throw new EntityNotFoundException("Project with id = " + projectId + " not found");
            }

            return;
        } else {
            throw new DataValidationException("Author or project is required");
        }
    }
}
