package faang.school.postservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {

    @Mock
    private Resource resource;

    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() throws IOException {
        File tempFile = File.createTempFile("banned-words", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("badword1\n");
            writer.write("badword2\n");
            writer.write("offensiveword\n");
        }

        when(resource.getFile()).thenReturn(tempFile);

        moderationDictionary = new ModerationDictionary(resource);
    }

    @Test
    void containsBannedWords_shouldReturnTrueWhenContentContainsBannedWord() {
        String content = "This is a badword1 in the text.";
        boolean result = moderationDictionary.containsBannedWords(content);

        assertThat(result).isTrue();
    }

    @Test
    void containsBannedWords_shouldReturnFalseWhenContentDoesNotContainBannedWord() {
        String content = "This is a clean text without bad words.";
        boolean result = moderationDictionary.containsBannedWords(content);

        assertThat(result).isFalse();
    }

    @Test
    void containsBannedWords_shouldHandleEmptyContentGracefully() {
        String content = "";
        boolean result = moderationDictionary.containsBannedWords(content);

        assertThat(result).isFalse();
    }

    @Test
    void containsBannedWords_shouldBeCaseInsensitive() {
        String content = "This is a BADWORD1 in the text.";
        boolean result = moderationDictionary.containsBannedWords(content);

        assertThat(result).isTrue();
    }
}