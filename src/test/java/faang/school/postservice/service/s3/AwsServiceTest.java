package faang.school.postservice.service.s3;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class AwsServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private AwsService awsService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_KEY = "test-file.txt";
    private InputStream mockInputStream;

    @BeforeEach
    void setUp() {
        byte[] mockData = "data".getBytes();
        mockInputStream = new ByteArrayInputStream(mockData);
    }

    @Test
    void testUploadFile_Success() {
        awsService.uploadFile(BUCKET_NAME, FILE_KEY, mockInputStream);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_IOException() throws IOException {
        InputStream failingStream = mock(InputStream.class);
        when(failingStream.readAllBytes()).thenThrow(new IOException());

        assertThrows(RuntimeException.class, () -> awsService.uploadFile(BUCKET_NAME, FILE_KEY, failingStream));
    }

    @Test
    void testDownloadFile_Success() {
        ResponseInputStream<GetObjectResponse> mockResponseStream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(), mockInputStream);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponseStream);

        InputStream downloadedStream = awsService.downloadFile(BUCKET_NAME, FILE_KEY);

        assertNotNull(downloadedStream);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    void testDeleteFile_Success() {
        awsService.deleteFile(BUCKET_NAME, FILE_KEY);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
