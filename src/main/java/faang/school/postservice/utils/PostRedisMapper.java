package faang.school.postservice.utils;

import faang.school.postservice.dto.posts.PostRedis;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface PostRedisMapper {
    Post toEntity(PostRedis postRedis);
    PostRedis toDto(Post post);
}
