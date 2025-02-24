package faang.school.postservice.service.comment;

import faang.school.postservice.exceptions.ModerationFileException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ModerationBlackListDictionary {

    private final Set<String> blacklist = new HashSet<>();
    @Value("${moderation.dictionary-black.path}")
    private String path;

    @PostConstruct
    private void loadBlackList() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                blacklist.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            log.error("Failed to load blacklist words", e);
            throw new ModerationFileException("Failed to load blacklist words");
        }
    }

    public boolean containsBadWord(String text) {
        if (text != null) {
            String lowerCaseText = text.toLowerCase();
            return blacklist.stream().anyMatch(lowerCaseText::contains);
        }
        return false;
    }

}

