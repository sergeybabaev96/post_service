package faang.school.postservice.service.util;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Сервис для изменения размера изображений.
 * Поддерживает пропорциональное масштабирование с сохранением соотношения сторон.
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
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Вычисляем новые размеры с сохранением пропорций
        int newWidth;
        int newHeight;
        if (originalWidth > originalHeight) {
            newWidth = maxSize;
            newHeight = (int) ((double) originalHeight / originalWidth * maxSize);
        } else {
            newHeight = maxSize;
            newWidth = (int) ((double) originalWidth / originalHeight * maxSize);
        }

        // Создаем новое изображение
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }
}
