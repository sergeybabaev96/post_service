package faang.school.postservice.service.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    @Override
    public void cacheHeat() {
        // redisTemplate.opsForZSet().range("user:feed:3", 0, -1);
    }

    @Override
    public void getNewsFeed() {
//        // 1. Получаем ID постов (opsForValue)
//        List<Post> posts = postRepository.findRecent();
//
//        // 2. Собираем ID авторов
//        Set<Long> authorIds = posts.stream()
//                .map(Post::getAuthorId)
//                .collect(Collectors.toSet());
//
//        // 3. Получаем всех авторов за 1 запрос (opsForHash)
//        Map<Long, Author> authors = authorCacheService.batchGetAuthors(authorIds);
//
//        // 4. Собираем результат
//        return posts.stream()
//                .map(post -> new PostWithAuthor(
//                        post,
//                        authors.get(post.getAuthorId())
//                ))
//                .collect(Collectors.toList());
    }
}
