package faang.school.postservice.controller;

import faang.school.postservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {

    @Value("${file.upload.max-size}")
    private int maxSize;

    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String uploadFile(@RequestBody MultipartFile file) {
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File is to big. Max size is " + maxSize);
        }

        return fileService.uploadFile(file);
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("key") String fileKey) {
        String fileName = URLEncoder.encode(
                    Path.of(fileKey).getFileName().toString(),
                    StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(
                        fileService.downloadFile(fileKey)));
    }
}
