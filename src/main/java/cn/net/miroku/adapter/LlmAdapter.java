package cn.net.miroku.adapter;

import cn.net.miroku.dto.chat.completion.MirokuRequest;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public abstract class LlmAdapter {
    /** http客户端 */
    protected final OkHttpClient okHttpClient;

    /**
     * 是否支持
     */
    public abstract boolean support(String model);

    /**
     * 创建聊天补全
     */
    public abstract Response createChatCompletion(MirokuRequest mirokuRequest) throws IOException;
}
