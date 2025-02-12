package faang.school.postservice.dto.spell;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpellDto implements Serializable {

    @JsonProperty("code")
    private int code;

    @JsonProperty("pos")
    int pos;

    @JsonProperty("row")
    int row;

    @JsonProperty("col")
    int col;

    @JsonProperty("len")
    int len;

    @JsonProperty("s")
    List<String> suggestions;
}
