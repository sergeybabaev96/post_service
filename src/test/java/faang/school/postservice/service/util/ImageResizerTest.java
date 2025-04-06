package faang.school.postservice.service.util;

import org.imgscalr.Scalr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты изменения размера изображений")
public class ImageResizerTest {

    private ImageResizer imageResizer = new ImageResizer();

    @Nested
    @DisplayName("Пропорциональное масштабирование")
    class ProportionalResizingTests {

        @ParameterizedTest
        @MethodSource("provideImageData")
        @DisplayName("При изменении размера сохраняются пропорции")
        void givenImageWithDimensions_WhenResize_ThenMaintainAspectRatio(
                int originalWidth,
                int originalHeight,
                int maxSize,
                int expectedWidth,
                int expectedHeight
        ) {
            BufferedImage original = new BufferedImage(
                    originalWidth,
                    originalHeight,
                    BufferedImage.TYPE_INT_RGB
            );

            BufferedImage resized = imageResizer.resize(original, maxSize);

            assertAll(
                    () -> assertEquals(expectedWidth, resized.getWidth(),
                            "Ширина не соответствует ожидаемой"),
                    () -> assertEquals(expectedHeight, resized.getHeight(),
                            "Высота не соответствует ожидаемой"),
                    () -> assertEquals(original.getType(), resized.getType(),
                            "Тип изображения изменился")
            );
        }

        private static Stream<Arguments> provideImageData() {
            return Stream.of(
                    // Альбомная ориентация (width > height)
                    Arguments.of(2000, 1000, 1000, 1000, 500),

                    // Портретная ориентация (height > width)
                    Arguments.of(800, 1200, 600, 400, 600),

                    // Квадратное изображение
                    Arguments.of(1500, 1500, 500, 500, 500),

                    // Граничный случай: maxSize больше оригинала
                    Arguments.of(300, 400, 800, 600, 800)
            );
        }
    }

    @Test
    @DisplayName("При вызове resize делегирует работу Scalr с правильными параметрами")
    void givenValidImage_WhenResize_ThenSuccess() {
        try (MockedStatic<Scalr> mockedScalr = Mockito.mockStatic(Scalr.class)) {
            BufferedImage original = new BufferedImage(1000, 500, BufferedImage.TYPE_INT_RGB);
            BufferedImage expected = new BufferedImage(500, 250, BufferedImage.TYPE_INT_RGB);

            mockedScalr.when(() -> Scalr.resize(
                    any(BufferedImage.class),
                    eq(Scalr.Method.QUALITY),
                    eq(Scalr.Mode.AUTOMATIC),
                    eq(500)
            )).thenReturn(expected);

            BufferedImage result = imageResizer.resize(original, 500);

            assertSame(expected, result, "Должно вернуться изображение из Scalr");
            mockedScalr.verify(() -> Scalr.resize(
                    original,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    500
            ));
        }
    }

    @Test
    @DisplayName("При передаче null изображения выбрасывается исключение")
    void givenNullImage_WhenResize_ThenThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> imageResizer.resize(null, 100),
                "Должно выбрасываться исключение для null изображения"
        );
    }
}