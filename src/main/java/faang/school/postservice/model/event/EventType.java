package faang.school.postservice.model.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
    PUBLISHED_POST("publishedPost");

    private final String key;
}