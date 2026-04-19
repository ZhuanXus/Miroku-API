package cn.net.miroku.adapter;

import cn.net.miroku.dto.ChatCompletionRequest;
import cn.net.miroku.dto.ChatCompletionResponse;

import java.io.IOException;

public interface LlmAdapter {
    boolean support(String model);
    ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) throws IOException;
}
