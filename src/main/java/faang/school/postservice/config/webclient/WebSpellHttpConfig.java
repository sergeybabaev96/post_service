package faang.school.postservice.config.webclient;

import faang.school.postservice.config.post.PostServiceConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@Getter
@RequiredArgsConstructor
public class WebSpellHttpConfig {
    @Value("${webspell.api.url}")
    private String webSpellApiUrl;

    @Value("${webspell.api.content-type}")
    private String webSpellApiContentType;

    @Value("${webspell.api.key}")
    private String webSpellApiKey;

    @Value("${webspell.api.host}")
    private String webSpellApiHost;

    @Bean
    public HttpClient webSpellHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(PostServiceConstants.CHECK_SPELLING_TIMEOUT))
                .build();
    }
}
