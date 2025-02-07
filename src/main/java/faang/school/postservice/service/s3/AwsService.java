package faang.school.postservice.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsService {
    private final S3Client s3;

    public void uploadFile(String bucketName, String keyName, InputStream file) {
        RequestBody requestBody;
        try {
            requestBody = RequestBody.fromBytes(file.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PutObjectRequest putOb = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();


        s3.putObject(putOb, requestBody);
        log.info("File uploaded to bucket({}): {}", bucketName, keyName);
    }

    public InputStream downloadFile(String bucketName, String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
        InputStream inputStream = s3.getObject(getObjectRequest);

        log.info("File {} was downloaded from bucket: ({})", keyName, bucketName);
        return inputStream;
    }

    public void deleteFile(String bucketName, String keyName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();
        s3.deleteObject(request);
        log.info("File deleted from bucket({}): {}", bucketName, keyName);
    }
}
