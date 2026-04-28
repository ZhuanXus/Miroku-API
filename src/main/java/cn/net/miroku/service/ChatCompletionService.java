package cn.net.miroku.service;

import cn.net.miroku.dto.chat.completion.Request;
import okhttp3.Response;

import java.io.IOException;

public interface ChatCompletionService {
    /**
     * 创建聊天补全
     */
    Response createChatCompletion(Request request) throws IOException;

    /**
     * 保存聊天补全
     */
    void saveChatCompletion(cn.net.miroku.dto.chat.completion.Response response);
}
