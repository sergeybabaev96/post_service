package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Компонент для валидации изображений.
 * Выполняет проверку загружаемых файлов изображений на соответствие требованиям.
 *
 * <p>Основные проверки:</p>
 * <ul>
 *   <li>Файл не должен быть пустым</li>
 *   <li>Размер файла не должен превышать 5MB</li>
 *   <li>Файл должен быть изображением (MIME-тип начинается с 'image/')</li>
 * </ul>
 *
 * @see DataValidationException
 *
 *  * @author Zhltsk-V
 *  * @version 1.0
 */
@Component
public class ValidateImage {

    /**
     * Проверяет валидность файла изображения.
     *
     * @param file файл для проверки
     * @throws DataValidationException если файл не соответствует требованиям
     */
    public void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("Uploaded file is empty");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new DataValidationException("File size must not exceed 5MB");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new DataValidationException("Only image files are allowed");
        }
    }
}
