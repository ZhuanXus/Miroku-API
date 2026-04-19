package cn.net.miroku.service;

import cn.net.miroku.dto.ChatCompletionRequest;
import cn.net.miroku.dto.ChatCompletionResponse;
import cn.net.miroku.adapter.LlmAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatCompletionService {
    private final List<LlmAdapter> llmStrategies;

    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) throws IOException {
        for (LlmAdapter strategy : llmStrategies) {
            if (strategy.support(request.getModel())) {
                return strategy.createChatCompletion(request);
            }
        }
        return null;
    }
}
