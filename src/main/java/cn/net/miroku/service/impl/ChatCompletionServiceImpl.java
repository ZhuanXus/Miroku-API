package cn.net.miroku.service.impl;

import cn.net.miroku.dto.ChatCompletionRequest;
import cn.net.miroku.adapter.LlmAdapter;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatCompletionServiceImpl implements cn.net.miroku.service.ChatCompletionService {
    private final List<LlmAdapter> llmStrategies;

    public Response createChatCompletion(ChatCompletionRequest request) throws IOException {
        for (LlmAdapter strategy : llmStrategies) {
            if (strategy.support(request.getModel())) {
                return strategy.createChatCompletion(request);
            }
        }
        return null;
    }
}
