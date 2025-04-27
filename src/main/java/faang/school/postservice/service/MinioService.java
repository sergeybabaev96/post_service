package faang.school.postservice.service;

import faang.school.postservice.exception.MinioException;
import faang.school.postservice.exception.ResizeImageException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    public void uploadFile(InputStream inputStream, String key, Map<String, String> metadata,
                           String contentType, String bucket) {
        try {
            long processedSize = inputStream.available();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(inputStream, processedSize, -1)
                            .contentType(contentType)
                            .userMetadata(metadata)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioException("An error occurred while uploading the image to Minio: " + e.getMessage());
        }
    }

    public void deleteFile(String fileKey, String bucket) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileKey)
                    .build());
            log.info("File {} has been deleted from MinIO", fileKey);
        } catch (Exception e) {
            throw new MinioException("Error deleting file: " + e.getMessage());
        }
    }

    public InputStream getFile(String fileKey, String bucket) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileKey)
                    .build());
        } catch (Exception e) {
            throw new MinioException("Error receiving file from MinIO: " + e.getMessage());
        }
    }

    public InputStream compressImage(InputStream imageInputStream, int width, int height, double outputQuality) {
        ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(imageInputStream)
                    .size(width, height)
                    .outputFormat("jpeg")
                    .outputQuality(outputQuality)
                    .toOutputStream(thumbnailOutputStream);

            return new ByteArrayInputStream(thumbnailOutputStream.toByteArray());
        } catch (IOException e) {
            throw new ResizeImageException("An occurred error while compressing the image");
        }
    }

    public String generateStorageKey(MultipartFile file, String folderName) {
        return String.format("%s/%s-%s",
                folderName,
                UUID.randomUUID(),
                file.getOriginalFilename());
    }
}