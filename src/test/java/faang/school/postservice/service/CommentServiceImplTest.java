package faang.school.postservice.service;

import faang.school.postservice.config.context.AsyncConfig;
import faang.school.postservice.dto.moderation.ItemModerationResultDto;
import faang.school.postservice.dto.moderation.ItemToVerifyDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    private static class CommentServiceTestableImpl extends CommentServiceImpl {
        public CommentServiceTestableImpl(AsyncConfig asyncConfig, ModerationService moderationService,
                CommentMapper commentMapper, CommentRepository commentRepository) {
            super(asyncConfig, moderationService, commentMapper, commentRepository);
        }

        @Override
        protected PageRequest createPageRequest(int page, int pageSize) {
            return PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        }
    }

    public static final Integer TEST_PAGE_SIZE = 10;
    public static final String TEXT_WITHOUT_RUDE_WORDS = "текст без грубостей";
    public static final String RUDE_WORD = "плохое_слово";
    public static final String TEXT_WITH_RUDE_WORD = "Текст с " + RUDE_WORD;

    @Mock
    private AsyncConfig asyncConfig;

    @Mock
    private ModerationService moderationService;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private CommentServiceTestableImpl commentService;

    private Executor testExecutor;

    @BeforeEach
    void setUp() {
        testExecutor = Runnable::run;

        ReflectionTestUtils.setField(commentService, "pageSize", TEST_PAGE_SIZE);
    }

    @Test
    public void shouldModerateComments_shouldProcessAndUpdateComments() {
        // Arrange
        List<Comment> comments = List.of(
                Comment.builder().id(1L).content(TEXT_WITHOUT_RUDE_WORDS).build(),
                Comment.builder().id(2L).content(TEXT_WITH_RUDE_WORD).build());

        var itemToVerify1 = commentMapper.toItemToVerifyDto(comments.get(0));
        var itemToVerify2 = commentMapper.toItemToVerifyDto(comments.get(1));
        List<ItemToVerifyDto> itemsToVerify = List.of(itemToVerify1, itemToVerify2);

        var verifiedItemResult = new ItemModerationResultDto(itemToVerify1, true);
        var unverifiedItemResult = new ItemModerationResultDto(itemToVerify2, false);
        List<ItemModerationResultDto> moderationResults = List.of(verifiedItemResult, unverifiedItemResult);

        when(commentRepository.countAllByVerifiedDateIsNull()).thenReturn(itemsToVerify.size());
        when(moderationService.moderateItems(any()))
                .thenReturn(CompletableFuture.completedFuture(moderationResults));

        when(asyncConfig.getAsyncExecutor()).thenReturn(testExecutor);

        // Act
        commentService.moderateComments();

        // Assert
        verify(commentRepository).countAllByVerifiedDateIsNull();
        verify(moderationService).moderateItems(any());

        var verifiedIdsCaptor = ArgumentCaptor.forClass(List.class);
        verify(commentRepository).updateVerifiedStatusAndDateByIds(
                verifiedIdsCaptor.capture(), any(LocalDateTime.class), eq(true));

        var unverifiedIdsCaptor = ArgumentCaptor.forClass(List.class);
        verify(commentRepository).updateVerifiedStatusAndDateByIds(
                unverifiedIdsCaptor.capture(), any(LocalDateTime.class), eq(false));

        assertEquals(List.of(comments.get(0).getId()), verifiedIdsCaptor.getValue());
        assertEquals(List.of(comments.get(1).getId()), unverifiedIdsCaptor.getValue());
    }

    @Test
    public void shouldModerateComments_doNotPerformActions_whenThereAreNotComments() {
        when(commentRepository.countAllByVerifiedDateIsNull()).thenReturn(0);
        commentService.moderateComments();

        verify(commentRepository).countAllByVerifiedDateIsNull();
        verifyNoMoreInteractions(commentRepository, moderationService);
    }
}