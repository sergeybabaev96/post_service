package faang.school.postservice.service;

import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.dto.like.PostLikeDto;
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
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;

    public void likePost(PostLikeDto postLikeDto) {
        likeValidator.validateUserExists(postLikeDto.getUserId());
        likeValidator.validatePostExists(postLikeDto.getPostId());

        Post post = postService.getPostById(postLikeDto.getPostId());

        if (likeRepository.findByPostIdAndUserId(postLikeDto.getPostId(), postLikeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this post.");
        }

        createAndSaveLike(likeMapper.toLike(postLikeDto), post, null);
    }

    @Transactional
    public void unlikePost(PostLikeDto postLikeDto) {
        likeValidator.validateUserExists(postLikeDto.getUserId());
        likeValidator.validatePostExists(postLikeDto.getPostId());

        likeRepository.deleteByPostIdAndUserId(postLikeDto.getPostId(), postLikeDto.getUserId());
    }

    public void likeComment(CommentLikeDto commentLikeDto) {
        likeValidator.validateUserExists(commentLikeDto.getUserId());
        likeValidator.validateCommentExists(commentLikeDto.getCommentId());

        Comment comment = commentService.getCommentById(commentLikeDto.getCommentId());

        if (likeRepository.findByCommentIdAndUserId(commentLikeDto.getCommentId(), commentLikeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this comment.");
        }

        createAndSaveLike(likeMapper.toLike(commentLikeDto), null, comment);
    }

    @Transactional
    public void unlikeComment(CommentLikeDto commentLikeDto) {
        likeValidator.validateUserExists(commentLikeDto.getUserId());
        likeValidator.validateCommentExists(commentLikeDto.getCommentId());

        likeRepository.deleteByCommentIdAndUserId(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
    }

    private void createAndSaveLike(Like like, Post post, Comment comment) {
        if (post != null) {
            like.setPost(post);
        }
        if (comment != null) {
            like.setComment(comment);
        }
        likeRepository.save(like);
    }
}