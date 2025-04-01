package faang.school.postservice.config.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class ModerationConfig {

    private Set<String> rudeWords = new HashSet<>();
}
