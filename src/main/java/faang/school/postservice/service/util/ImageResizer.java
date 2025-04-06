package faang.school.postservice.service.util;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * Сервис для изменения размера изображений.
 * Использует библиотеку ImgScalr для обработки изображений.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>{@link #resize} - Изменение размера изображения с сохранением пропорций</li>
 * </ul>
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Service
public class ImageResizer {

    /**
     * Изменяет размер изображения, сохраняя пропорции.
     * Размеры вычисляются так, чтобы самая большая сторона равнялась указанному значению.
     *
     * @param originalImage исходное изображение
     * @param maxSize       максимальный размер (в пикселях) для самой большой стороны
     * @return новое изображение с измененными размерами
     */
    public BufferedImage resize(BufferedImage originalImage, int maxSize) {
        return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, maxSize);
    }
}