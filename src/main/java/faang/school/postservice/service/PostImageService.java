package faang.school.postservice.service;

import faang.school.postservice.config.minio.PostsImagesMinioProperties;
import faang.school.postservice.exception.InvalidFileTypeException;
import faang.school.postservice.exception.ReadingImageException;
import faang.school.postservice.exception.UploadFileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.repository.adapter.ResourceRepositoryAdapter;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostImageService {

    private final PostValidator postValidator;
    private final PostsImagesMinioProperties postsImagesMinioProperties;
    private final MinioService minioService;
    private final ResourceRepositoryAdapter resourceRepositoryAdapter;
    private final ResourceRepository resourceRepository;
    private final PostRepositoryAdapter postRepositoryAdapter;

    @Transactional
    public void addingImagesToAPost(Long postId, List<MultipartFile> images) {
        Post post = findPostAndAuthorValidation(postId);

        log.info("List files = {} ", images);
        if (images.size() > 10) {
            throw new UploadFileException("You can add at least 1 and no more than 10 photos to a post");
        }

        images.forEach(this::validateImage);

        images.stream()
                .map(image -> uploadImage(image, post))
                .forEach(resourceRepository::save);
    }

    @Transactional
    public void deleteImageFromAPost(Long postId, Long resourceId) {
        findPostAndAuthorValidation(postId);

        Resource resource = resourceRepositoryAdapter.findResourceById(resourceId);
        checkingIfAResourceExistForAPost(resource, postId);

        minioService.deleteFile(resource.getKey(), postsImagesMinioProperties.getBucketName());
        resourceRepository.delete(resource);
    }

    @Transactional
    public InputStream getImage(Long resourceId) {
        Resource resource = resourceRepositoryAdapter.findResourceById(resourceId);
        log.debug("User downloading image with ID {}", resourceId);
        return minioService.getFile(resource.getKey(), postsImagesMinioProperties.getBucketName());
    }

    private Post findPostAndAuthorValidation(Long postId) {
        Post post = postRepositoryAdapter.getById(postId);
        postValidator.postAuthorValidation(post);
        return post;
    }

    private Resource uploadImage(MultipartFile image, Post post) {
        try (InputStream inputStream = image.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);

            int[] originalSize = {originalImage.getWidth(), originalImage.getHeight()};
            int[] maxSize = getMaxDimensionsForOrientation(originalImage);

            InputStream processedImageStream = (originalSize[0] <= maxSize[0] && originalSize[1] <= maxSize[1])
                    ? image.getInputStream() : minioService.compressImage(image.getInputStream(), maxSize[0], maxSize[1],
                    postsImagesMinioProperties.getOutputQuality());

            String key = minioService.generateStorageKey(image, postsImagesMinioProperties.getFolderName());
            Resource resource = Resource.builder()
                    .key(key)
                    .name(image.getContentType())
                    .post(post)
                    .build();

            Map<String, String> metadata = generateMetadata(originalSize, maxSize);

            minioService.uploadFile(processedImageStream, key, metadata, image.getContentType(),
                    postsImagesMinioProperties.getBucketName());

            return resource;
        } catch (IOException e) {
            throw new ReadingImageException("Error processing image");
        }
    }

    private Map<String, String> generateMetadata(int[] originalSize, int[] maxSize) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Original-Width", String.valueOf(originalSize[0]));
        metadata.put("Original-Height", String.valueOf(originalSize[1]));
        metadata.put("Max-Width", String.valueOf(maxSize[0]));
        metadata.put("Max-Height", String.valueOf(maxSize[1]));
        return metadata;
    }

    private void validateImage(MultipartFile image) {
        if (image.getSize() > postsImagesMinioProperties.getMaxImageSize()) {
            throw new DataValidationException("The image size exceeds the maximum limit of 5 MB");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException("Uploading allowed only for images");
        }
    }

    private int[] getMaxDimensionsForOrientation(BufferedImage image) {
        if (image.getWidth() > image.getHeight()) {
            return new int[]{
                    postsImagesMinioProperties.getMaxHorizontalWidth(),
                    postsImagesMinioProperties.getMaxHorizontalHeight()
            };
        }
        return new int[]{
                postsImagesMinioProperties.getMaxSquareDimensions(),
                postsImagesMinioProperties.getMaxSquareDimensions()
        };
    }

    private void checkingIfAResourceExistForAPost(Resource resource, long postId) {
        if (resource.getPost().getId() != postId) {
            throw new DataValidationException("This resource belongs to another post");
        }
    }
}
