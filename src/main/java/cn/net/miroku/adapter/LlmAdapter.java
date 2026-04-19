package cn.net.miroku.adapter;

import cn.net.miroku.dto.ChatCompletionRequest;
import okhttp3.Response;

import java.io.IOException;

public interface LlmAdapter {
    boolean support(String model);

    /**
     * 创建聊天补全
     */
    Response createChatCompletion(ChatCompletionRequest request) throws IOException;
}
