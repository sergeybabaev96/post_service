package faang.school.postservice.controller.feed;

import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final FeedService feedService;

}
