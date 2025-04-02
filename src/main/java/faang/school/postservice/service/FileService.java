package faang.school.postservice.service;

import com.fasterxml.jackson.datatype.jdk8.WrappedIOException;
import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String FILE_KEY_PATTERN= "USER_ID_%s/%s-%s";
    private static final String DEFAULT_USER_ID = "DEFAULT";
    private static final String SMALL_IMAGE_KEY_PATTERN = "small/%s";

    private static final List<String> IMAGE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".wbmp");
    private static final int WIDTH_OF_IMAGE_PX = 300;

    private final S3Client s3Client;
    private final UserContext userContext;


    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;


    @SneakyThrows
    public String uploadFile(MultipartFile file)  {
        String fileKey = getFileKey(file);

        putObjectInS3Client(file, fileKey);

        if (isFileAnImage(file)) {
            putObjectInS3Client(
                    makeThumbnail(file)
                    , String.format(SMALL_IMAGE_KEY_PATTERN, fileKey));
        }

        return fileKey;
    }

    public InputStream downloadFile(String fileKey) {
        return s3Client.getObject(
                getGetObjectResponse(fileKey));
    }


    @SneakyThrows
    private MultipartFile makeThumbnail(MultipartFile file) {
        InputStream inputStream = file.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        Thumbnails.of(bufferedImage)
                .size(WIDTH_OF_IMAGE_PX, getNewHeightOfImage(bufferedImage, WIDTH_OF_IMAGE_PX))
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        String fileName = file.getOriginalFilename()+".jpg";
        String contentType = "image/jpeg";
        return new MyMultipartFile(outputStream.toByteArray(), file.getName(), fileName, contentType);
    }

    private int getNewHeightOfImage(BufferedImage bufferedImage, double newWidth) {
        double newHeight = bufferedImage.getHeight() * newWidth / bufferedImage.getWidth();
        return (int) Math.ceil(newHeight);
    }

    private boolean isFileAnImage(MultipartFile file) {
        String lowerCaseName = file.getOriginalFilename().toLowerCase();
        for (String extension : IMAGE_EXTENSIONS) {
            if (lowerCaseName.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    private void putObjectInS3Client(MultipartFile file, String fileKey) {
        try {
            s3Client.putObject(
                    getPutObjectRequest(file, fileKey), getRequestBody(file));
        } catch (IOException e) {
            throw new WrappedIOException(e);
        }
    }

    private PutObjectRequest getPutObjectRequest(MultipartFile file, String fileKey) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();
    }

    private RequestBody getRequestBody(MultipartFile file) throws IOException {
        return RequestBody.fromInputStream(file.getInputStream(), file.getSize());
    }

    private GetObjectRequest getGetObjectResponse(String fileKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
    }

    private String getFileKey(MultipartFile file) {
        String pathName = DEFAULT_USER_ID;
        String timeStamp = String.valueOf(System.nanoTime());

        return String.format(FILE_KEY_PATTERN, pathName, timeStamp, file.getOriginalFilename());
    }
}
