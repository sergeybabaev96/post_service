package faang.school.postservice.service;

import faang.school.postservice.dto.moderation.ItemModerationResultDto;
import faang.school.postservice.dto.moderation.ItemToVerifyDto;
import faang.school.postservice.moderator.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {
    private final ModerationDictionary moderationDictionary;

    @Async("taskExecutor")
    public CompletableFuture<List<ItemModerationResultDto>> moderateItems(
            Supplier<Stream<ItemToVerifyDto>> itemsFetcher) {
        return CompletableFuture.completedFuture(itemsFetcher.get()
                .map(dto ->
                {
                    var words = dto.content().split(" ");
                    var haveRudeWords = Arrays.stream(words)
                            .map(String::trim)
                            .anyMatch(moderationDictionary::containsWord);

                    return new ItemModerationResultDto(dto, !haveRudeWords);
                })
                .toList());
    }
}
