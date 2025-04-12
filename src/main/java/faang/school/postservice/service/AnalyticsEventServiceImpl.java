package faang.school.postservice.service;

import faang.school.postservice.dto.analytic.AnalyticCreateEventDto;
import faang.school.postservice.dto.analytic.AnalyticEventDto;
import faang.school.postservice.dto.analytic.AnalyticFilterDto;
import faang.school.postservice.filter.AnalyticsEventFilter;
import faang.school.postservice.mapper.AnalyticsEventMapper;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import faang.school.postservice.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;


/**
 * Сервис для работы с лайками.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #saveEvent(AnalyticCreateEventDto)} )} - Сохраняет аналитику</li>
 *   <li>{@link #getAnalytics(AnalyticFilterDto)} - Получает аналитику по заданным параметрам</li>
 * </ul>
 *
 * @author takewqa
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventServiceImpl implements AnalyticsEventService {
    private final AnalyticsEventRepository analyticsEventRepository;
    private final AnalyticsEventMapper analyticsEventMapper;
    private final List<AnalyticsEventFilter> analyticsEventFilters;

    /**
     * Сохраняет аналитику
     * @param analyticCreateEventDto Сущность для создания аналитики
     */
    @Override
    public void saveEvent(AnalyticCreateEventDto analyticCreateEventDto) {
        analyticsEventRepository.save(analyticsEventMapper.toEntity(analyticCreateEventDto));
        log.debug("Event saved {}", analyticCreateEventDto);
    }

    /**
     * Получение аналитики по заданным параметрам
     * @param filter Фильтр для поиска
     * @return {@link List<AnalyticEventDto>} Отфильтрованный список с аналитикой
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticEventDto> getAnalytics(AnalyticFilterDto filter) {
        Stream<AnalyticsEvent> eventsStream =
                analyticsEventRepository.findByAuthorIdAndReceiverId(filter.authorId(), filter.receiverId());

        for (AnalyticsEventFilter eventFilter : analyticsEventFilters) {
            if (eventFilter.isApplicable(filter)){
                eventsStream = eventFilter.apply(eventsStream, filter);
            }
        }

        return analyticsEventMapper.toAnalyticsEventDtoList(eventsStream);
    }
}
