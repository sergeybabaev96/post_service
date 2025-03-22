package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.сomment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;


    @Transactional
    public CommentDto createComment(@Valid CommentDto commentDto) {
        log.info("Creating a comment for post ID: {} by user ID: {}", commentDto.getPostId(), commentDto.getAuthorId());
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new DataValidationException("Post not found with ID: " + commentDto.getPostId()));

        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        log.info("Getting user author comment : {}", userDto);
        if (userDto == null) {
            throw new DataValidationException("User not found with ID: " + commentDto.getAuthorId());
        }

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        log.info("Saved comment : {}", savedComment);
        return commentMapper.toDto(savedComment);

    }
}
