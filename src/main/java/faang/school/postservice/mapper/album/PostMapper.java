package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PostMapper {

    public Post toEntity(PostDto dto);

    public PostDto toDto(Post entity);
}
