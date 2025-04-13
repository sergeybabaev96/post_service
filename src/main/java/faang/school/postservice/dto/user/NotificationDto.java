package faang.school.postservice.dto.user;

import lombok.Data;

@Data
public class NotificationDto {
    private UserDto user;
    private String message;
}
