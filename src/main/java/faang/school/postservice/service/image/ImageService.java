package faang.school.postservice.service.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    void resizeAndUploadImage(String key, Boolean isSmall, MultipartFile file);
}
