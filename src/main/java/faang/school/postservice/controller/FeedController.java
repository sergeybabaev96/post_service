package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.service.NewsFeedService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/v1/feeds")
@RestController
@RequiredArgsConstructor
public class FeedController {
    private final NewsFeedService newsFeedService;
    private final UserContext userContext;

    @GetMapping
    public List<UserFeedDto> getUserFeed(
            @RequestParam(value = "last-post", required = false) @Valid @Positive Long lastPostId,
            @RequestParam(value = "size", defaultValue = "20") @Valid @Positive int size
    ) {
        return newsFeedService.getUserFeed(userContext.getUserId(), lastPostId, size);
    }
}
