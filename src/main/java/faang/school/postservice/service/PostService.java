package faang.school.postservice.service;

import faang.school.postservice.api.PerspectiveAPI;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PerspectiveAPI perspectiveAPI;

    public void moderatePosts() {
        List<Post> notVerifiedPosts = postRepository.findByVerified(false);
        try {
            boolean isToxic = perspectiveAPI.isContentToxic("fuck");
            if (isToxic) {
                log.info("TOXIC");
            } else {
                log.info("NOT TOXIC");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
