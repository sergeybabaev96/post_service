package faang.school.postservice;

import faang.school.postservice.model.Post;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

public class PostImageConstantsIT {
    public static final long AUTHOR_ID = 2L;
    public static final long ANOTHER_AUTHOR_ID = 5L;
    public static final long ANOTHER_AUTHOR_ID_2 = 9L;
    public static final long PROJECT_ID = 100L;
    public static final long RESOURCE_ID = 10L;
    public static final long NON_EXISTENT_POST_ID = 1000L;
    public static final long NON_EXISTENT_RESOURCE_ID = 3000L;
    public static final byte[] TEST_IMAGE_BYTES = "fake jpeg image".getBytes(StandardCharsets.UTF_8);

    public static final List<MockMultipartFile> ELEVEN_IMAGES = IntStream.rangeClosed(1, 11)
            .mapToObj(i -> new MockMultipartFile(
                    "files",
                    "IMAGE" + i + ".jpg",
                    "image/jpeg",
                    new byte[]{(byte) (i), (byte) (i + 1), (byte) (i + 2)}
            ))
            .toList();

    public static final List<MockMultipartFile> IMAGES_FOR_POST = List.of(
            new MockMultipartFile("files", "TEST_IMAGE_1.jpeg",
                    "image/jpeg", createTestImageBytes("jpeg")),
            new MockMultipartFile("files", "TEST_IMAGE_2.png",
                    "image/png", createTestImageBytes("png")));

    public static final List<MockMultipartFile> IMAGE_EXCEED_SIZE = List.of(
            new MockMultipartFile("files", "EXCEED_SIZE_IMAGE.jpg",
                    "image/jpeg", new byte[6 * 1024 * 1024]));

    public static final List<MockMultipartFile> INVALID_CONTENT = List.of(
            new MockMultipartFile("files", "TEST_VIDEO.mp4",
                    "video/mp4", new byte[]{1, 2, 3}));

    public static final Post EXISTENT_POST = Post.builder()
            .content("Some content")
            .authorId(AUTHOR_ID)
            .build();

    public static final Post ANOTHER_POST = Post.builder()
            .content("Another content")
            .authorId(ANOTHER_AUTHOR_ID)
            .build();

    public static final Post ANOTHER_POST_2 = Post.builder()
            .content("Another content 2")
            .authorId(ANOTHER_AUTHOR_ID_2)
            .build();

    private static byte[] createTestImageBytes(String format) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0xFF0000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, format, baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
}
