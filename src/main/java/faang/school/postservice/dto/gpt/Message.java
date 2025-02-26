package faang.school.postservice.dto.gpt;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @NotNull
    private String role;
    @NotNull
    private String content;
}
