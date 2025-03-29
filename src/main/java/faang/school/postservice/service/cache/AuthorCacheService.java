package faang.school.postservice.service.cache;

import faang.school.postservice.dto.user.UserDto;

import java.util.Optional;

public interface AuthorCacheService {

    void cacheAuthor(Long authorId, UserDto user);

    Optional<UserDto> getCachedAuthor(Long authorId);
}
