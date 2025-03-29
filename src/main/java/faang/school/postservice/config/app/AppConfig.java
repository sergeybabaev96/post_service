package faang.school.postservice.config.app;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppConfig {

    @Value("${app.settings.max-length}")
    private int maxLength;

}
