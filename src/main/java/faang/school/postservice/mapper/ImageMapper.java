package faang.school.postservice.mapper;

import faang.school.postservice.dto.Post.UploadedImageResponseDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    @Mapping(target = "postId", source = "post.id")
    UploadedImageResponseDto toDto(Resource resource);
}
