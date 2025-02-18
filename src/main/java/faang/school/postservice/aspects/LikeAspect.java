package faang.school.postservice.aspects;

import faang.school.event.Event;
import faang.school.event.NotificationLikeEvent;
import faang.school.postservice.annotations.PublishLikeEvent;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.publisher.like.NotificationLikeEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LikeAspect {

    private final Map<Class<? extends Event>, EventPublisher> eventPublisherMap;

    public LikeAspect(NotificationLikeEventPublisher notificationPublisher) {
        eventPublisherMap = Map.of(NotificationLikeEvent.class, notificationPublisher);
    }

    @AfterReturning(pointcut = "@annotation(publishLikeEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishLikeEvent publishLikeEvent, Object result) {
        if (result == null) {
            log.info("Method {} returned null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }
        Arrays.stream(publishLikeEvent.events()).forEach(eventClass -> {
            EventPublisher publisher = eventPublisherMap.get(eventClass);
            publisher.publishEvent(result);
        });
    }
}