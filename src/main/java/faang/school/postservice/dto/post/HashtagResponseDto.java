package faang.school.postservice.dto.post;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record HashtagResponseDto(
        @JsonProperty("name")
        String name,

        @JsonProperty("postsIds")
        List<Long> postsIds) {

}
