package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class UserDto implements Serializable {

    private Long id;

    private String username;

    private String email;

    private String phone;

}
