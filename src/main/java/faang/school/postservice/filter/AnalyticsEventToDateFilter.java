package faang.school.postservice.filter;

import faang.school.postservice.dto.analytic.AnalyticFilterDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AnalyticsEventToDateFilter implements AnalyticsEventFilter {

    @Override
    public boolean isApplicable(AnalyticFilterDto filter) {
        return filter.to() != null;
    }

    @Override
    public Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> stream, AnalyticFilterDto filter) {
        return stream
                .filter(analyticsEvent -> analyticsEvent.getCreatedAt().isBefore(filter.to()));
    }
}
