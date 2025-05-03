package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostService {

    PostDto createDraft(PostDto postDto);

    PostDto getPost(long postId);

    PostDto publishPost(long postId);

    PostDto updatePost(long postId, String content);

    void softDeletePost(long postId);

    List<PostDto> getAllDraftsByAuthorId(long authorId);

    List<PostDto> getAllDraftsByProjectId(long projectId);

    List<PostDto> getAllPostsByAuthorId(long authorId);

    List<PostDto> getAllPostsByProjectId(long projectId);

    Post findPostById(long postId);
}
