package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ad.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;

    public void likePost(Long postId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeValidator.validatePostExists(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        if (likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this post.");
        }

        saveLike(createLikeForPost(likeDto, post));
    }

    @Transactional
    public void unlikePost(Long postId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());
    }

    public void likeComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeValidator.validateCommentExists(commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this comment.");
        }

        saveLike(createLikeForComment(likeDto, comment));
    }

    @Transactional
    public void unlikeComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
    }

    private Like createLikeForPost(LikeDto likeDto, Post post) {
        Like like = likeMapper.toLike(likeDto);
        like.setPost(post);
        return like;
    }

    private Like createLikeForComment(LikeDto likeDto, Comment comment) {
        Like like = likeMapper.toLike(likeDto);
        like.setComment(comment);
        return like;
    }

    private void saveLike(Like like) {
        likeRepository.save(like);
    }
}