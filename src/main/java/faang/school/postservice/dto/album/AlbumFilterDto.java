package faang.school.postservice.dto.album;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class AlbumFilterDto {

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfCreation;
    private String title;

}
