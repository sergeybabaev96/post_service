package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataInvalidException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostDto createDraft(PostDto postDto) {
        validatePostDto(postDto);
        //Post post = postMapper.toEntity(postDto);
       Post savedPost = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(savedPost);
    }

    private void validatePostDto(PostDto postDto) {
        if (postDto == null) {
            throw new DataInvalidException("postDto cannot be null");
        } if (postDto.content() == null || postDto.content().isBlank()) {
            throw new DataInvalidException("Post content cannot be empty");
        } if (postDto.authorId() == null) {
            throw new DataInvalidException("author id cannot be null");
        }
    }
}
