package faang.school.postservice.service.impl;

import faang.school.postservice.client.AiClient;
import faang.school.postservice.dto.gpt.ChatRequest;
import faang.school.postservice.dto.gpt.Message;
import faang.school.postservice.model.Post;
import faang.school.postservice.properties.AIProperties;
import faang.school.postservice.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private static final String SYSTEM = "system";
    private static final String USER = "user";

    private final AIProperties aiProperties;
    private final AiClient aiClient;

    @Override
    public String checkGrammarPost(Post post) {
        log.debug("Start check grammar post id = {}, text = {}", post.getId(), post.getContent());

        return aiClient
                .chat(createChatRequest(post.getContent()))
                .findAssistanceContent();
    }

    private ChatRequest createChatRequest(String text) {
        return new ChatRequest(aiProperties.getModel(), false,
                List.of(new Message(SYSTEM, aiProperties.getGrammarPrompt()), new Message(USER, text)));
    }
}
