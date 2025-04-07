package faang.school.postservice.dto.album;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class PostDto {

    private Long id;
    private String title;
    private String description;
}
