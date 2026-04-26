package cn.net.miroku.adapter;

import cn.net.miroku.dto.chat.completion.Request;
import okhttp3.Response;

import java.io.IOException;

public abstract class LlmAdapter {


    public abstract boolean support(String model);

    /**
     * 创建聊天补全
     */
    public abstract Response createChatCompletion(Request request) throws IOException;
}
