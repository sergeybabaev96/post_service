package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.model.Comment;

import faang.school.postservice.service.util.ImageProcessor;
import faang.school.postservice.validation.CommentValidator;
import faang.school.postservice.validation.ValidateImage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;


/**
 * Сервис для управления изображениями комментариев.
 * Координирует процессы загрузки, обработки и хранения изображений, прикрепленных к комментариям.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>{@link #uploadImage} - Полный цикл обработки изображения от валидации до сохранения</li>
 *   <li>{@link #deleteImage} - Удаление изображений комментария с очисткой ссылок</li>
 * </ul>
 *
 * <p>Используемые компоненты:</p>
 * <ul>
 *   <li>{@link ImageProcessor} - для обработки и конвертации изображений</li>
 *   <li>{@link S3StorageService} - для работы с объектным хранилищем</li>
 *   <li>{@link ValidateImage} - для валидации входящих файлов</li>
 *   <li>{@link CommentValidator} - для проверки бизнес-правил</li>
 * </ul>
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentImageService {
    private static final String IMAGE_CONTENT_TYPE = "image/jpeg";
    private static final String KEY_PREFIX = "comments/";

    private final CommentService commentService;
    private final S3StorageService storageService;
    private final ImageProcessor imageProcessor;
    private final ValidateImage validateImage;
    private final CommentValidator commentValidator;

    /**
     * Загружает и обрабатывает изображение для комментария.
     *
     * @param postId    ID поста, к которому относится комментарий
     * @param commentId ID комментария для прикрепления изображения
     * @param file      загружаемый файл изображения
     * @return DTO комментария с обновленными ссылками на изображения
     * @throws DataValidationException  если файл не проходит валидацию
     * @throws EntityNotFoundException  если комментарий или пост не найдены
     * @throws ImageProcessingException при ошибках обработки изображения
     */
    @Transactional
    public CommentViewDto uploadImage(Long postId, Long commentId, MultipartFile file) {
        validateImage.validateImageFile(file);
        Comment comment = getValidatedComment(postId, commentId);

        BufferedImage originalImage = null;
        try {
            originalImage = imageProcessor.convertToBufferedImage(file);
        } catch (IOException exception) {
            log.error("Failed to convert image: {}", exception.getMessage());
            throw new ImageProcessingException("Failed to convert image");
        }
        ImageProcessor.ProcessedImages processedImages = imageProcessor.processImage(originalImage);

        String baseKey = generateFileKey(commentId);
        uploadProcessedImages(baseKey, processedImages);

        return updateCommentWithImages(comment, baseKey);
    }

    /**
     * Удаляет изображения, прикрепленные к комментарию.
     *
     * @param postId    ID поста, к которому относится комментарий
     * @param commentId ID комментария для удаления изображений
     * @return DTO комментария с очищенными ссылками на изображения
     * @throws EntityNotFoundException если комментарий или пост не найдены
     */
    @Transactional
    public CommentViewDto deleteImage(Long postId, Long commentId) {
        Comment comment = getValidatedComment(postId, commentId);

        try {
            deleteCommentImages(comment);
        } catch (ImageProcessingException e) {
            log.warn("Failed to delete images from storage: {}", e.getMessage());
        }
        return clearImageReferences(comment);
    }

    /**
     * Получает и валидирует комментарий.
     *
     * @param postId    ID поста для проверки принадлежности
     * @param commentId ID комментария
     * @return найденный комментарий
     * @throws EntityNotFoundException если комментарий не существует
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     */
    private Comment getValidatedComment(Long postId, Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId);
        return comment;
    }

    /**
     * Генерирует уникальный ключ для хранения изображений.
     *
     * @param commentId ID комментария
     * @return сгенерированный ключ в формате "comments/{commentId}/{uuid}"
     */
    private String generateFileKey(Long commentId) {
        return KEY_PREFIX + commentId + "/" + UUID.randomUUID();
    }

    /**
     * Загружает обработанные изображения в хранилище.
     *
     * @param baseKey базовый ключ для формирования имен файлов
     * @param images  обработанные версии изображения
     * @throws ImageProcessingException при ошибках загрузки
     */
    private void uploadProcessedImages(String baseKey, ImageProcessor.ProcessedImages images) {
        try {
            storageService.uploadToS3(baseKey + "-large.jpg", images.largeImage(), IMAGE_CONTENT_TYPE);
            storageService.uploadToS3(baseKey + "-small.jpg", images.smallImage(), IMAGE_CONTENT_TYPE);
        } catch (IOException exception) {
            log.error("Failed to upload processed images: {}", exception.getMessage());
            throw new ImageProcessingException("Failed to upload processed images");
        }
    }

    /**
     * Обновляет комментарий ссылками на изображения.
     *
     * @param comment комментарий для обновления
     * @param baseKey базовый ключ для формирования ссылок
     * @return обновленный DTO комментария
     */
    private CommentViewDto updateCommentWithImages(Comment comment, String baseKey) {
        comment.setLargeImageFileKey(baseKey + "-large.jpg");
        comment.setSmallImageFileKey(baseKey + "-small.jpg");
        return commentService.updateCommentEntity(comment);
    }

    /**
     * Удаляет изображения комментария из хранилища.
     *
     * @param comment комментарий с изображениями
     */
    private void deleteCommentImages(Comment comment) {
        if (comment.getLargeImageFileKey() != null) {
            storageService.deleteFile(comment.getLargeImageFileKey());
        }
        if (comment.getSmallImageFileKey() != null) {
            storageService.deleteFile(comment.getSmallImageFileKey());
        }
    }

    /**
     * Очищает ссылки на изображения в комментарии.
     *
     * @param comment комментарий для очистки
     * @return обновленный DTO комментария
     */
    private CommentViewDto clearImageReferences(Comment comment) {
        comment.setLargeImageFileKey(null);
        comment.setSmallImageFileKey(null);
        return commentService.updateCommentEntity(comment);
    }
}