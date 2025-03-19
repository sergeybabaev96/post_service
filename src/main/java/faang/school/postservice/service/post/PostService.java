package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    ProjectServiceClient projectServiceClient;
    UserServiceClient userServiceClient;

    public PostDto createPost(PostDto postDto) {

        // Проверить, чтобы был либо автор, либо проект
        userServiceClient.getUser(postDto.getAuthorId());
        projectServiceClient.getProject(postDto.getProjectId());


        return new PostDto();
    }

    public PostDto publicPost(PostDto postDto) {

        // 1. Пост уже должен существовать
        // 2. Если пост уже опубликован, его больше нельзя публиковать

        return new PostDto();
    }


}
