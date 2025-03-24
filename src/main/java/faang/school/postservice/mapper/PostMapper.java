package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    Post createDtoToEntity(PostCreateDto postCreateDto);

    @Mapping(source = "likes", target = "likeCounter")
    PostViewDto toViewDto(Post post);

    void update(PostUpdateDto source, @MappingTarget Post target);

    default long getLikeCounts(List<Like> likes){
        return likes.size();
    }

}
