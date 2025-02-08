package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AlbumUsersDto(@NotNull @NotEmpty List<Long> usersIds) {
}
