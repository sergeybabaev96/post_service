package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/feeds")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public FeedResponse getNewsFeed(@RequestParam(required = false) long postId) {
        return feedService.getNewsFeed(postId);
    }
}
