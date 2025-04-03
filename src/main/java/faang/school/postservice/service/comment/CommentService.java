package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(@Valid CommentCreateDto commentCreateDto) {
        log.info("Creating a comment for post ID: {} by user ID: {}", commentCreateDto.getPostId(), commentCreateDto.getAuthorId());

        if (!postRepository.existsById(commentCreateDto.getPostId())) {
            throw new EntityNotFoundException("Post with ID " + commentCreateDto.getPostId() + " does not exist.");
        }

        try {
            UserDto userDto = userServiceClient.getUser(commentCreateDto.getAuthorId());
            if (userDto == null) {
                throw new EntityNotFoundException("User not found with ID: " + commentCreateDto.getAuthorId());
            }

            // Находим объект Post по ID
            Post post = postRepository.findById(commentCreateDto.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));

            // Преобразуем DTO в сущность Comment
            Comment comment = commentMapper.commentCreateDtoToComment(commentCreateDto, post);

            // Сохраняем комментарий
            Comment savedComment = commentRepository.save(comment);
            log.info("Comment created with ID: {}", savedComment.getId());

            // Преобразуем обратно в DTO для вывода
            return commentMapper.commentToDto(savedComment);

        } catch (FeignException e) {
            log.error("Error while fetching user from userService: {}", e.getMessage());
            throw new EntityNotFoundException("Error while verifying user existence: " + e.getMessage());
        }
    }

    // Другие методы остаются без изменений...
}