package faang.school.postservice.dto.album;

import faang.school.postservice.model.Post;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AlbumDto {

    private Long id;
    private Long authorId;
    private String title;
    private String description;
    private List<Post> posts;
}
