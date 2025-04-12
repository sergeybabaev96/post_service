package faang.school.postservice.mapper;

import faang.school.postservice.dto.analytic.AnalyticCreateEventDto;
import faang.school.postservice.dto.analytic.AnalyticEventDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface AnalyticsEventMapper {

    AnalyticsEvent toEntity(AnalyticCreateEventDto analyticCreateEventDto);

    List<AnalyticEventDto> toAnalyticsEventDtoList(Stream<AnalyticsEvent> analyticsEvents);
}
