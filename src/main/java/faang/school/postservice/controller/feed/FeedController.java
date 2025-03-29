package faang.school.postservice.controller.feed;

import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/feeds")
@Validated
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/heat")
    public void feedCacheHeat() {
        feedService.cacheHeat();
    }

    public void getNewsFeed() {
        feedService.getNewsFeed();
    }
}
