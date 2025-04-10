package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.InvalidFileException;
import faang.school.postservice.exception.MaxResourcesReachedException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.exception.not_found_exceptions.PostNotFoundException;
import faang.school.postservice.exception.not_found_exceptions.ResourceNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.messages.ExceptionMessages;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static faang.school.postservice.messages.ValidationMessages.VALIDATION_CONTENT_TYPE;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private static final Long BYTES_FILE_SIZE = 5L * 1024L * 1024L;
    private static final Integer MAX_COUNT_OF_RESOURCES = 10;
    private static final int STANDARD_WIDTH = 1080;
    private static final int HORIZONTAL_HEIGHT = 566;
    private static final int VERTICAL_SQUARE_HEIGHT = 1080;
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;
    private final ResourceMapper resourceMapper;
    private final MinioService minioService;

    @Transactional
    public List<ResourceDto> add(Long postId, List<MultipartFile> files) {
        files.forEach(this::validateFile);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionMessages.POST_NOT_FOUND_EXCEPTION));

        if (post.getResources().size() == MAX_COUNT_OF_RESOURCES) {
            log.error(ExceptionMessages.RESOURCE_MAX_LIMIT_EXCEPTION);
            throw new MaxResourcesReachedException(ExceptionMessages.RESOURCE_MAX_LIMIT_EXCEPTION);
        }

        List<Resource> uploadedResource = new ArrayList<>();

        for (MultipartFile file : files) {
            String key = file.getOriginalFilename() + System.currentTimeMillis();
            Resource resource = processAndUploadFile(file, key);

            resource.setPost(post);
            post.getResources().add(resource);
            uploadedResource.add(resource);
        }

        log.info("The file was added to the post");
        return resourceMapper.toResourceDtoList(uploadedResource);
    }

    public void delete(Long postId, Long resourceId) {
        Resource resource = postResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND_EXCEPTION));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionMessages.POST_NOT_FOUND_EXCEPTION));
        String key = resource.getKey();

        if (!postId.equals(resource.getPost().getId())) {
            log.error(ExceptionMessages.POST_ID_MISMATCH_EXCEPTION);
            throw new PostIdMismatchException(
                    ExceptionMessages.POST_ID_MISMATCH_EXCEPTION
            );
        }

        post.getResources().remove(resource);
        postResourceRepository.deleteById(resourceId);
        postRepository.save(post);
        minioService.delete(key);
        log.info("The file has been removed from the bucket");
    }

    private Resource processAndUploadFile(MultipartFile file, String key) {
        String contentType = file.getContentType();

        if (contentType == null) {
            log.error(VALIDATION_CONTENT_TYPE);
            throw new InvalidFileException(VALIDATION_CONTENT_TYPE);
        }

        if (file.getOriginalFilename() == null) {
            log.error(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
        }

        String fileExtension = file.getOriginalFilename().
                substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        if (contentType.startsWith("image")) {
            try {
                BufferedImage croppedImage = resizeImage(file);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(croppedImage, fileExtension, outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                    return minioService.uploadImage(inputStream, imageBytes, key, file.getName(), contentType);
                }
            } catch (IOException e) {
                throw new InvalidFileException(ExceptionMessages.FILE_PROCESS_IMAGE_EXCEPTION);
            }
        }

        if (contentType.startsWith("video") || contentType.startsWith("audio")) {
            return minioService.uploadVideoOrAudio(file, key);
        } else {
            throw new InvalidFileException("Unsupported file type: " + contentType);
        }
    }

    private BufferedImage resizeImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        boolean isHorizontal = image.getWidth() > image.getHeight();

        int imageWidth = STANDARD_WIDTH;
        int imageHeight = isHorizontal ? HORIZONTAL_HEIGHT : VERTICAL_SQUARE_HEIGHT;

        if (image.getWidth() <= imageWidth && image.getHeight() <= imageHeight) {
            return image;
        }

        return Thumbnails.of(image)
                .size(imageWidth, imageHeight)
                .keepAspectRatio(false)
                .asBufferedImage();
    }

    private void validateFile(MultipartFile file) {
        if (file.getName().isBlank()) {
            log.error(ExceptionMessages.FILE_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_NAME_EMPTY_EXCEPTION);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            log.error(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
        }

        if (file.getSize() > BYTES_FILE_SIZE) {
            log.error(ExceptionMessages.FILE_SIZE_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_SIZE_EXCEPTION);
        }
    }
}
