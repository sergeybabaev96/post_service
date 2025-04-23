package faang.school.postservice.service.util;

import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Сервис для обработки изображений.
 * Создает уменьшенные копии изображения и конвертирует MultipartFile в BufferedImage.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>{@link #processImage(BufferedImage)} - обрабатывает изображение, создавая его уменьшенные копии</li>
 *   <li>{@link #convertToBufferedImage(MultipartFile)} - конвертирует MultipartFile в BufferedImage</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessor {
    private static final int LARGE_IMAGE_SIZE = 1080;
    private static final int SMALL_IMAGE_SIZE = 170;

    private final ImageResizer imageResizer;

    /**
     * Обрабатывает изображение, создавая его уменьшенные копии.
     *
     * @param originalImage оригинальное изображение
     * @return объект ProcessedImages с уменьшенными копиями
     */
    public ProcessedImages processImage(BufferedImage originalImage) {
        Objects.requireNonNull(originalImage, "Original image cannot be null");

        return new ProcessedImages(
                imageResizer.resize(originalImage, LARGE_IMAGE_SIZE),
                imageResizer.resize(originalImage, SMALL_IMAGE_SIZE)
        );
    }

    /**
     * Конвертирует MultipartFile в BufferedImage.
     *
     * @param file файл для конвертации
     * @return конвертированное изображение
     * @throws IOException если произошла ошибка при чтении файла
     */
    public BufferedImage convertToBufferedImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                log.error("Invalid image file: {}", file.getOriginalFilename());
                throw new DataValidationException("Invalid image file");
            }
            return image;
        }
    }
}