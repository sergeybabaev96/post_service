package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class PostViewAspect {
    private final PostViewEventPublisher publisher;
    private final UserContext userContext;

    @AfterReturning(pointcut = "@annotation(faang.school.postservice.service.annotation.ViewPost)", returning = "dto")
    public void publishPostView(ResponsePostDto dto) {
        log.info("Starting aspect");
        Long viewerId = userContext.getUserId();
        Long authorId = dto.getAuthorId();

        if (!Objects.equals(viewerId, authorId)) {
            PostViewEvent event = PostViewEvent.builder()
                    .postId(dto.getId())
                    .authorId(authorId)
                    .userId(viewerId)
                    .whenViewed(LocalDateTime.now())
                    .build();

            publisher.publish(event);
        }
    }
}