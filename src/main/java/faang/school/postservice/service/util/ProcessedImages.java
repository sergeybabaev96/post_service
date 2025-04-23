package faang.school.postservice.service.util;

import java.awt.image.BufferedImage;

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