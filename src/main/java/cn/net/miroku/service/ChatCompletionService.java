package cn.net.miroku.service;

import cn.net.miroku.dto.chat.completion.Request;
import okhttp3.Response;

import java.io.IOException;

public interface ChatCompletionService {
    /**
     * 创建聊天补全
     * @param request 请求参数
     * @return okhttp响应
     */
    Response createChatCompletion(Request request) throws IOException;
}
