package faang.school.postservice.service.post;

import faang.school.postservice.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {
    private static final String BLOCKED_WORDS_FILE = "blocked-words.txt";

    private final FileUtils fileUtils;
    private Set<String> blockedWords;

    @PostConstruct
    void readWordsFromFile() {
        blockedWords = fileUtils.getAllLines(BLOCKED_WORDS_FILE)
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    public boolean isAllowed(String content) {
        content = content.toLowerCase();
        return blockedWords.stream().noneMatch(content::contains);
    }
}
