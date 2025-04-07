package faang.school.postservice.controller;

import faang.school.postservice.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @Test
    void uploadFile() {
        String key = "12345";
        MultipartFile file = new MockMultipartFile(key, new byte[]{});
        Mockito.when(fileService.uploadFile(Mockito.any(MultipartFile.class))).thenReturn(key);

        assertEquals(key, fileController.uploadFile(file));
    }

    @Test
    void downloadFile() {
    }
}