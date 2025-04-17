package faang.school.postservice.service.util;

import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты класса ImageProcessor")
class ImageProcessorTest {

    @Mock
    private ImageResizer imageResizer;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageProcessor imageProcessor;

    @Nested
    @DisplayName("Тестирование метода processImage")
    class ProcessImageTests {
        @Test
        @DisplayName("Должен возвращать обработанные изображения (большое и маленькое) при валидном входе")
        void givenValidOriginalImage_WhenProcessImage_ThenReturnProcessedImages() {
            BufferedImage originalImage = createTestImage(2000, 1500);
            BufferedImage largeImage = createTestImage(1080, 810);
            BufferedImage smallImage = createTestImage(170, 128);

            when(imageResizer.resize(originalImage, 1080)).thenReturn(largeImage);
            when(imageResizer.resize(originalImage, 170)).thenReturn(smallImage);

            ProcessedImages result = imageProcessor.processImage(originalImage);

            assertNotNull(result);
            assertEquals(largeImage, result.largeImage());
            assertEquals(smallImage, result.smallImage());
            verify(imageResizer).resize(originalImage, 1080);
            verify(imageResizer).resize(originalImage, 170);
        }

        @Test
        @DisplayName("Должен выбрасывать NullPointerException при null входе")
        void givenNullImage_WhenProcessImage_ThenThrowNullPointerException() {
            assertThrows(NullPointerException.class,
                    () -> imageProcessor.processImage(null));
        }
    }

    @Nested
    @DisplayName("Тестирование метода convertToBufferedImage")
    class ConvertToBufferedImageTests {
        @Test
        @DisplayName("Должен возвращать BufferedImage при валидном файле изображения")
        void givenValidImageFile_WhenConvertToBufferedImage_ThenReturnBufferedImage() throws IOException {
            BufferedImage expectedImage = createTestImage(100, 100);
            InputStream inputStream = new ByteArrayInputStream(new byte[100]);

            when(multipartFile.getInputStream()).thenReturn(inputStream);

            try (var mockedImageIO = mockStatic(ImageIO.class)) {
                mockedImageIO.when(() -> ImageIO.read(inputStream)).thenReturn(expectedImage);

                BufferedImage result = imageProcessor.convertToBufferedImage(multipartFile);

                assertNotNull(result);
                assertEquals(expectedImage, result);
                verify(multipartFile).getInputStream();
            }
        }

        @Test
        @DisplayName("Должен выбрасывать DataValidationException при невалидном файле изображения")
        void givenInvalidImageFile_WhenConvertToBufferedImage_ThenThrowDataValidationException() throws IOException {
            InputStream inputStream = new ByteArrayInputStream(new byte[0]);
            when(multipartFile.getInputStream()).thenReturn(inputStream);

            try (var mockedImageIO = mockStatic(ImageIO.class)) {
                mockedImageIO.when(() -> ImageIO.read(inputStream)).thenReturn(null);

                assertThrows(DataValidationException.class,
                        () -> imageProcessor.convertToBufferedImage(multipartFile));
            }
        }

        @Test
        @DisplayName("Должен выбрасывать IOException при ошибке чтения потока")
        void givenFailingInputStream_WhenConvertToBufferedImage_ThenThrowIOException() throws IOException {
            when(multipartFile.getInputStream()).thenThrow(new IOException("Test error"));

            assertThrows(IOException.class,
                    () -> imageProcessor.convertToBufferedImage(multipartFile));
        }
    }

    private BufferedImage createTestImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
}