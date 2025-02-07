package faang.school.postservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {
    private final Set<String> bannedWords;

    public ModerationDictionary(@Value("classpath:moderation/banned-words.txt") Resource resource) throws IOException {
        this.bannedWords = new HashSet<>(Files.readAllLines(resource.getFile().toPath()));
    }

    public boolean containsBannedWords(String content) {
        return bannedWords.stream().anyMatch(content.toLowerCase()::contains);
    }
}