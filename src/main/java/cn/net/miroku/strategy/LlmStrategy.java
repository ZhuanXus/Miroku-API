package cn.net.miroku.strategy;

import cn.net.miroku.dto.ChatCompletionRequest;
import cn.net.miroku.dto.ChatCompletionResponse;

import java.io.IOException;

public interface LlmStrategy {
    boolean support(String model);
    ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) throws IOException;
}
