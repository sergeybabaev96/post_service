package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private final UserServiceClient userServiceClient;

    public void validateUserExist(Long userId) {
        if (userServiceClient.getUser(userId) == null) {
            log.warn("User with id {} is not found", userId);
            throw new EntityNotFoundException("User with id %d is not found".formatted(userId));
        }
    }
}
