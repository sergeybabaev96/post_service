package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testUser";
    private static final String EMAIL = "test@example.com";

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void testValidateUserExist_UserFound() {

        UserDto userDto = new UserDto(USER_ID, USERNAME, EMAIL);

        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        assertDoesNotThrow(() -> userValidator.validateUserExist(USER_ID));
        verify(userServiceClient, times(1)).getUser(USER_ID);
    }

    @Test
    public void testValidateUserExist_UserNotFound() {

        when(userServiceClient.getUser(USER_ID)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userValidator.validateUserExist(USER_ID));

        assertEquals("User with id 1 is not found", exception.getMessage());
        verify(userServiceClient, times(1)).getUser(USER_ID);
    }
}
