package faang.school.postservice.correcter;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostCorrecterService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class PostCorrecterTest {

    @InjectMocks
    private PostCorrecter postCorrecter;

    @Mock
    private PostService postService;

    @Mock
    private PostCorrecterService postCorrecterService;


    private final List<PostDto> posts = List.of(
            PostDto.builder().id(1L).build(),
            PostDto.builder().id(2L).build()
    );

    @Test
    void correctingSpellingOfPosts_ShouldCorrecting() {
        when(postService.getAllDraftPosts()).thenReturn(posts);

        assertDoesNotThrow(() -> postCorrecter.correctingSpellingOfPosts());
        verify(postService, times(1)).getAllDraftPosts();
        posts.forEach(post ->
                verify(postCorrecterService, times(1)).correctingSpellingPost(post));
    }

    @Test
    void correctingSpellingOfPosts_ShouldNothingWhenHaveNotDraftPosts() {
        when(postService.getAllDraftPosts()).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> postCorrecter.correctingSpellingOfPosts());
        verify(postService, times(1)).getAllDraftPosts();
        verify(postCorrecterService, never()).correctingSpellingPost(any());
    }
}