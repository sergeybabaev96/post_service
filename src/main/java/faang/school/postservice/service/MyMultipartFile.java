package faang.school.postservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyMultipartFile implements MultipartFile {

    private final byte[] fileBytes;
    private final String fileName;
    private final String originalFilename;
    private final String contentType;

    public MyMultipartFile(byte[] fileBytes, String fileName, String originalFilename, String contentType) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileBytes.length == 0;
    }

    @Override
    public long getSize() {
        return fileBytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileBytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileBytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try(OutputStream outputStream = new FileOutputStream(dest)) {
            outputStream.write(fileBytes);
        }
    }
}
