package faang.school.postservice.dto.album;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostDto {

    private MultipartFile image;
    private String title;
    private String description;
}
