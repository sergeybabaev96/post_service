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
 * Выполняет преобразование и подготовку изображений перед сохранением.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>{@link #processImage} - Создание уменьшенных копий изображения</li>
 *   <li>{@link #convertToBufferedImage} - Конвертация MultipartFile в BufferedImage</li>
 * </ul>
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessor {
    private final ImageResizer imageResizer;

    /**
     * Контейнер для хранения обработанных версий изображения.
     * Содержит:
     * <ul>
     *   <li>largeImage - изображение с максимальной стороной 1080px</li>
     *   <li>smallImage - изображение с максимальной стороной 170px</li>
     * </ul>
     */
    public record ProcessedImages(BufferedImage largeImage, BufferedImage smallImage) {
    }

    /**
     * Создает уменьшенные копии изображения.
     *
     * @param originalImage исходное изображение для обработки
     * @return контейнер с обработанными изображениями
     * @throws NullPointerException если originalImage равен null
     * @see ImageResizer#resize(BufferedImage, int)
     */
    public ProcessedImages processImage(BufferedImage originalImage) {
        Objects.requireNonNull(originalImage, "Original image cannot be null");

        return new ProcessedImages(
                imageResizer.resize(originalImage, 1080),
                imageResizer.resize(originalImage, 170)
        );
    }

    /**
     * Конвертирует MultipartFile в BufferedImage.
     *
     * @param file загруженный файл изображения
     * @return объект BufferedImage
     * @throws IOException             если произошла ошибка чтения файла
     * @throws DataValidationException если файл не является валидным изображением
     */
    public BufferedImage convertToBufferedImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new DataValidationException("Invalid image file");
            }
            return image;
        }
    }
}