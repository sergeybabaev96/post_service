package faang.school.postservice.service;

import faang.school.postservice.dto.analytic.AnalyticCreateEventDto;
import faang.school.postservice.dto.analytic.AnalyticEventDto;
import faang.school.postservice.dto.analytic.AnalyticFilterDto;

import java.util.List;

public interface AnalyticsEventService {

    void saveEvent(AnalyticCreateEventDto analyticCreateEventDto);

    List<AnalyticEventDto> getAnalytics(AnalyticFilterDto filter);
}
