package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.creatpost.CreatePostValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CreatePostValidator createPostValidator;

    @ResponseBody
    public Post createPost(Post post) {
        log.info("Creating post: {}", post);
        createPostValidator.validateIsPostCreator(post);
        return postRepository.save(post);
    }

//    public Post getPostById(Long id) {
//        return postRepository.findById(id)
//                .orElseThrow(() -> new DataValidationException("Post with id " + id + " not found"));
//    }
//
//    public List<PostDto> getPostsByAuthorId(Long authorId) {
//        List<Post> posts = postRepository.findByAuthorId(authorId);
//        return posts.stream()
//                .map(postMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    public List<PostDto> getPostsByProjectId(Long projectId) {
//        List<Post> posts = postRepository.findByProjectId(projectId);
//        return posts.stream()
//                .map(postMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    public void deletePostById(Long id) {
//        postRepository.deleteById(id);
//    }
}
