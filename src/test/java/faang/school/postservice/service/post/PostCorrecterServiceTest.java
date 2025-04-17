package faang.school.postservice.service.post;

import faang.school.postservice.client.speller.YandexSpellerClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.speller.SpellerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class PostCorrecterServiceTest {

    @InjectMocks
    private PostCorrecterService postCorrecterService;

    @Mock
    private YandexSpellerClient yandexSpellerClient;

    @Mock
    private PostService postService;

    private final long id = 1;
    private final String content = "cantent ssme";
    private final String updatedContent = "content some";
    private final PostDto postDto = PostDto.builder().id(id).content(content).build();
    private final List<SpellerDto> spellers = new ArrayList<>(List.of(
            SpellerDto.builder().pos(0).len(7).s(List.of("content")).build(),
            SpellerDto.builder().pos(8).len(4).s(List.of("some")).build()));

    @Test
    void correctingSpellingPost_ShouldCorrect() {
        when(yandexSpellerClient.checkSpelling(content)).thenReturn(spellers);
        ArgumentCaptor<PostDto> argumentCaptor = ArgumentCaptor.forClass(PostDto.class);

        assertDoesNotThrow(() -> postCorrecterService.correctingSpellingPost(postDto));
        verify(yandexSpellerClient, times(1)).checkSpelling(content);
        verify(postService, times(1)).updatePost(anyLong(), argumentCaptor.capture());
        assertEquals(updatedContent, argumentCaptor.getValue().getContent());
    }

    @Test
    void correctingSpellingPost_ShouldExceptionWhenPostIsNull() {
        assertThrows(NullPointerException.class, () -> postCorrecterService.correctingSpellingPost(null));
    }
}