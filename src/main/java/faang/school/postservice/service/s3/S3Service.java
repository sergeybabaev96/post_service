package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 s3;

    @Value("{services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folderName) {
      log.info("Uploading file {}", file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String key = String.format("%s%s%s", folderName, LocalDateTime.now(),
                file.getOriginalFilename());
        try{
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(),
                    objectMetadata);
            s3.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new FileException("Failed to upload file", e);
        }
        log.info("File {} uploaded successfully", file.getOriginalFilename());

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(file.getSize());
        return null;
    }
}
