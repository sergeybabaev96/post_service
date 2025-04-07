package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;

    public void likeThePost(long postId, long userId) {
        validationExistsAuthor(userId);
        validationExistsPost(postId);
        canLikePost(postId, userId);
        LikeDto likeDto = LikeDto.builder()
                //я не совсем понимаю как мы передаем ID для лайка, он автоматически встанет?
                //есть ли возможность его проверить?
                .authorId(userId)
                .postId(postId)
                .createdAt(LocalDateTime.now()).build();
        Like like = likeMapper.toEntity(likeDto);
        likeRepository.save(like);
    }
    public void likeTheComment(long commentId, long userId) {
        validationExistsAuthor(userId);
        validationExistsComment(commentId);
        canLikeComment(commentId, userId);
        LikeDto likeDto = LikeDto.builder()
                .authorId(userId)
                .commentId(commentId)
                .createdAt(LocalDateTime.now()).build();
        Like like = likeMapper.toEntity(likeDto);
        likeRepository.save(like);
    }
    public void removeLikeFromPost(long postId, long userId) {
        validationExistsAuthor(userId);
        validationExistsPost(postId);

        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }
    public void removeLikeFromComment(long commentId, long userId) {
        validationExistsAuthor(userId);
        validationExistsComment(commentId);
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    private void validationExistsAuthor(long authorId) {
        if (userServiceClient.getUser(authorId) == null) {
                throw new EntityNotFoundException("user with id " + authorId + " is not exists");
        }
    }

    private void validationExistsPost(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new EntityNotFoundException("post with id " + postId + " is not exists");
        }
    }

    private void validationExistsComment(long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new EntityNotFoundException("comment with id " + commentId + " is not exists");
        }
    }

    private void canLikePost(long postId, long userId) {
        Optional<Like> likePost = likeRepository.findByPostIdAndUserId(postId, userId);
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        boolean hasLikedComment = commentList.stream().
                anyMatch(comment ->
                        likeRepository.findByCommentIdAndUserId(comment.getId(), userId).isPresent());
        throwEntityExistsException(likePost.isPresent(), hasLikedComment, userId);
    }

    private void canLikeComment(long commentId, long userId) {
        Optional<Like> likeComment = likeRepository.findByCommentIdAndUserId(commentId, userId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        long postId = comment.get().getPost().getId();
        Optional<Like> likePost = likeRepository.findByPostIdAndUserId(postId, userId);
        throwEntityExistsException(likePost.isPresent(), likeComment.isPresent(), userId);
    }

    private void throwEntityExistsException(boolean post, boolean comment, long userId) {
        if (post) {
            throw new EntityExistsException("comment has already been liked by user with id"  + userId);
        }
        if (comment) {
            throw new EntityExistsException("post has already been liked by user with id"  + userId);
        }
    }
}
