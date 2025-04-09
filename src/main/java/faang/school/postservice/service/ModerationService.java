package faang.school.postservice.service;

import faang.school.postservice.dto.moderation.ItemModerationResultDto;
import faang.school.postservice.dto.moderation.ItemToVerifyDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ModerationService {
    CompletableFuture<List<ItemModerationResultDto>> moderateItems(Supplier<Stream<ItemToVerifyDto>> itemsFetcher);
}
