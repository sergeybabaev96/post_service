package faang.school.postservice.mapper.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.event.UserEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ResourceMapper.class)
public interface UserEventMapper {

    @Mapping(target = "ttl", ignore = true)
    UserEvent toEvent(UserDto userDto);
}
