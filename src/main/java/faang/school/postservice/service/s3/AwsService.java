package faang.school.postservice.service.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsService {
    private final AmazonS3 s3;

    public void uploadFile(String bucketName, String keyName, InputStream file, String contentType) throws AmazonClientException {
        long contentLength;
        try {
            contentLength = file.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        s3.putObject(bucketName, keyName, file, metadata);
        log.info("File uploaded to bucket({}): {}", bucketName, keyName);
    }

    public InputStream downloadFile(String bucketName, String keyName) throws AmazonClientException{
        S3Object s3Object = s3.getObject(bucketName, keyName);
        InputStream inputStream = s3Object.getObjectContent();
        log.info("File {} was downloaded from bucket: ({})", keyName, bucketName);
        return inputStream;
    }

    public void deleteFile(String bucketName, String keyName) throws AmazonClientException{
        s3.deleteObject(bucketName, keyName);
        log.info("File deleted from bucket({}): {}", bucketName, keyName);
    }
}
