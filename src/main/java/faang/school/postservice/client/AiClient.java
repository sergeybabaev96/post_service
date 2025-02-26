package faang.school.postservice.client;

import faang.school.postservice.dto.gpt.ChatRequest;
import faang.school.postservice.dto.gpt.ChatResponse;

public interface AiClient {
    ChatResponse chat(ChatRequest chatRequest);
}
