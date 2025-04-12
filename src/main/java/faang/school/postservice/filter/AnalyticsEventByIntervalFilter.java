package faang.school.postservice.filter;

import faang.school.postservice.dto.analytic.AnalyticFilterDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class AnalyticsEventByIntervalFilter implements AnalyticsEventFilter {

    @Override
    public boolean isApplicable(AnalyticFilterDto filter) {
        return filter.interval() != null;
    }

    @Override
    public Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> stream, AnalyticFilterDto filter) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromInterval = now.minusSeconds(filter.interval().getSeconds());
        return stream
                .filter(analyticsEvent -> {
                    LocalDateTime created = analyticsEvent.getCreatedAt();
                    return created.isAfter(fromInterval) && created.isBefore(now);
                });
    }
}
