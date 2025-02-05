package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.s3.AwsService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final AwsService awsService;
    private final ResourceService resourceService;

    private final CommentValidator commentValidator;
    private final ImageProcessor imageProcessor;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${image.comment.largeImageMaxSize}")
    private int LARGE_IMAGE_MAX_SIZE;

    @Value("${image.comment.smallImageMaxSize}")
    private int SMALL_IMAGE_MAX_SIZE;

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        postService.get(postId);

        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
    }

    @Transactional
    public Comment createComment(Comment comment, Long postId, Long authorId) {
        Post post = postService.get(postId);
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            throw new UserNotFoundException("User with id = " + authorId + " was not found");
        }

        comment.setPost(post);
        comment.setAuthorId(authorId);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment updatedComment, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id; " + commentId));

        commentValidator.validateAuthor(comment, userId);
        commentValidator.validateCommentUpdate(updatedComment);

        String updatedContent = updatedComment.getContent();
        comment.setContent(updatedContent);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id + " + commentId));
        commentValidator.validateAuthor(comment, userId);

        commentRepository.delete(comment);

        return comment;
    }

    @Transactional
    public Comment attachImageToComment(Long commentId, MultipartFile image, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id: " + commentId));

        commentValidator.validateAuthor(comment, userId);
        commentValidator.validateImageFormat(image);

        String fileName = image.getOriginalFilename();
        String format = fileName.substring(fileName.lastIndexOf('.') + 1);
        String key = "/posts/" + comment.getPost().getId() + "/" + fileName + "_" + LocalDateTime.now();

        InputStream smallImage;
        InputStream largeImage;
        try {
            BufferedImage large = imageProcessor.resizeImage(image, LARGE_IMAGE_MAX_SIZE);
            BufferedImage small = imageProcessor.resizeImage(image, SMALL_IMAGE_MAX_SIZE);

            largeImage = imageProcessor.convertInputStream(large, format);
            smallImage = imageProcessor.convertInputStream(small, format);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String keyLarge = key + "_large" + "." + format;
        String keySmall = key + "_small" + "." + format;
        awsService.uploadFile(bucketName, keyLarge, largeImage, image.getContentType());
        awsService.uploadFile(bucketName, keySmall, smallImage, image.getContentType());

        Resource large = Resource.builder()
                .key(keyLarge)
                .type(format)
                .size(image.getSize())
                .name(fileName + "_l")
                .post(comment.getPost())
                .build();

        Resource small = Resource.builder()
                .key(keySmall)
                .type(format)
                .size(image.getSize())
                .name(fileName + "_s")
                .post(comment.getPost())
                .build();

        resourceService.createResource(large);
        resourceService.createResource(small);

        comment.setLargeImageFileKey(keyLarge);
        comment.setSmallImageFileKey(keySmall);

        return commentRepository.save(comment);
    }

    @Transactional
    public byte[] getCommentImage(@PathVariable Long commentId, Function<Comment, String> keyExtractor) throws IOException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id " + commentId));

        String key = keyExtractor.apply(comment);
        InputStream file = awsService.downloadFile(bucketName, key);

        return file.readAllBytes();
    }


    @Transactional
    public Comment deleteCommentImage(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id: " + commentId));

        commentValidator.validateAuthor(comment, userId);

        String smallKey = comment.getSmallImageFileKey();
        String largeKey = comment.getLargeImageFileKey();

        awsService.deleteFile(bucketName, smallKey);
        awsService.deleteFile(bucketName, largeKey);

        comment.setSmallImageFileKey(null);
        comment.setLargeImageFileKey(null);
        commentRepository.save(comment);

        Resource small = resourceService.findResourceByKey(smallKey);
        Resource large = resourceService.findResourceByKey(largeKey);
        resourceService.deleteResource(small);
        resourceService.deleteResource(large);
        return comment;
    }
}
