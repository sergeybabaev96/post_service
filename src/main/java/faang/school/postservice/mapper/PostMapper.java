package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Identifiable;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostEvent;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {

    PostDto toDto(Post post);

    Post toEntity(PostDto post);

    PostEvent toEvent(Post postEvent);

    default Long toId(Identifiable identifiable) {
        if (identifiable == null) {
            return null;
        }
        return identifiable.getId();
    }
}