package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpellCheckResponse {
    private String word;
    private List<String> s;
    private int pos;
    private int len;
}
