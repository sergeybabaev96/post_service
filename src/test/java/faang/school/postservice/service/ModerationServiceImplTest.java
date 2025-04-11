package faang.school.postservice.service;

import faang.school.postservice.dto.moderation.ItemModerationResultDto;
import faang.school.postservice.dto.moderation.ItemToVerifyDto;
import faang.school.postservice.moderator.ModerationDictionary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationServiceImplTest {

    public static final String TEXT_WITHOUT_RUDE_WORDS = "текст без грубостей";
    public static final String RUDE_WORD = "плохое_слово";
    public static final String TEXT_WITH_RUDE_WORD = "Текст с " + RUDE_WORD;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private ModerationServiceImpl moderationService;

    @Test
    void shouldModerateItems_returnAllVerifiedItems_whenContentDoesNotContainRudeWords() {
        // Arrange
        when(moderationDictionary.containsWord(anyString())).thenReturn(false);

        var item = new ItemToVerifyDto(1L, TEXT_WITHOUT_RUDE_WORDS);
        Supplier<Stream<ItemToVerifyDto>> supplier = () -> Stream.of(item);

        // Act
        var results = moderationService.moderateItems(supplier).join();

        // Assert
        assertEquals(1, results.size());
        ItemModerationResultDto result = results.get(0);
        assertEquals(item, result.item());
        assertTrue(result.isVerified());
    }

    @Test
    void shouldModerateItems_returnNonVerifiedItems_whenContentContainsRudeWords() {
        // Arrange
        when(moderationDictionary.containsWord("Текст")).thenReturn(false);
        when(moderationDictionary.containsWord("с")).thenReturn(false);
        when(moderationDictionary.containsWord(RUDE_WORD)).thenReturn(true);

        ItemToVerifyDto item = new ItemToVerifyDto(1L, TEXT_WITH_RUDE_WORD);
        Supplier<Stream<ItemToVerifyDto>> supplier = () -> Stream.of(item);

        // Act
        var results = moderationService.moderateItems(supplier).join();

        // Assert
        assertEquals(1, results.size());
        assertFalse(results.get(0).isVerified());
    }

    @Test
    void shouldModerateItems_returnsEmptyList_whenEmptyStreamIsPassed() {
        Supplier<Stream<ItemToVerifyDto>> supplier = Stream::empty;
        var results = moderationService.moderateItems(supplier).join();

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldModerateItems_returnVerifiedItem_whenContentIsEmpty() {
        ItemToVerifyDto item = new ItemToVerifyDto(1L, "");
        Supplier<Stream<ItemToVerifyDto>> supplier = () -> Stream.of(item);
        var results = moderationService.moderateItems(supplier).join();

        assertEquals(1, results.size());
        assertTrue(results.get(0).isVerified());
    }

    @Test
    void shouldModerateItems_returnDifferentKindOfItems_whenDifferentKindOfItemsArePassed() {
        // Arrange
        when(moderationDictionary.containsWord(anyString())).thenReturn(false);
        when(moderationDictionary.containsWord(RUDE_WORD)).thenReturn(true);

        var itemWithoutRudeWords = new ItemToVerifyDto(1L, TEXT_WITHOUT_RUDE_WORDS);
        var itemWithRudeWords = new ItemToVerifyDto(2L, TEXT_WITH_RUDE_WORD);
        var itemWithoutRudeWordsAgain = new ItemToVerifyDto(3L, "ещё текст");

        Supplier<Stream<ItemToVerifyDto>> supplier = () -> Stream.of(itemWithoutRudeWords, itemWithRudeWords,
                itemWithoutRudeWordsAgain);

        // Act
        var results = moderationService.moderateItems(supplier).join();

        // Assert
        assertEquals(3, results.size());
        assertTrue(results.get(0).isVerified());
        assertFalse(results.get(1).isVerified());
        assertTrue(results.get(2).isVerified());
    }
}