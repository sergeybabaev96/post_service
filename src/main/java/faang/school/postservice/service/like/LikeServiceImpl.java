package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.Publication;
import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserContext userContext;
    private final LikeValidator likeValidator;

    @Override
    public LikePostDtoResponse createLikeForPost(LikePostDto likePostDto) {

        final long userId = getUserId(likePostDto);
        final long postId = likePostDto.postId();
        log.info("Creating a user id = {}, like for a post = {}", userId, postId);

        likeValidator.validateUserExists(userId);

        Post post = likeValidator.validateAndGetPost(postId);
        likeValidator.validatePostLiked(postId, userId);

        final Like like = likeMapper.toLike(likePostDto);
        like.setPost(post);

        final Like savedLike = likeRepository.save(like);

        log.info("UserId = {} successfully liked postId = {} with {} ", userId, postId, savedLike);
        return likeMapper.toLikePostDtoResponse(savedLike);
    }

    @Override
    public LikeCommentDtoResponse createLikeForComment(LikeCommentDto likeCommentDto) {

        final long userId = getUserId(likeCommentDto);
        final long commentId = likeCommentDto.commentId();
        log.info("Creating like with userId = {} like for a commentId = {}", userId, commentId);

        likeValidator.validateUserExists(userId);

        Comment comment = likeValidator.validateAndGetComment(commentId);
        likeValidator.validateCommentLiked(commentId, userId);

        final Like like = likeMapper.toLike(likeCommentDto);
        like.setComment(comment);

        final Like savedLike = likeRepository.save(like);

        log.info("UserId = {} successfully liked commentId = {} with {}", userId, commentId, savedLike);
        return likeMapper.toLikeCommentDtoResponse(savedLike);
    }

    @Override
    @Transactional
    public void deleteLikeFromPost(long postId) {

        final long userId = getUserId();
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Successfully deleted like for postId = {} by userId = {}", postId, userId);
    }

    @Override
    @Transactional
    public void deleteLikeFromComment(long commentId) {

        final long userId = getUserId();
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Successfully deleted like for commentId = {} by userId = {}", commentId, userId);
    }

    private long getUserId(Publication publication) {

        return publication.userId();
    }

    private long getUserId() {

        final long userId = userContext.getUserId();
        likeValidator.validateUserId(userId);
        return userId;
    }
}