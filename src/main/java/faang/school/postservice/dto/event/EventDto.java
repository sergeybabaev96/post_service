package faang.school.postservice.dto.event;

import faang.school.postservice.model.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private long eventId;
    private long authorId;
    private EventType eventType;
}
