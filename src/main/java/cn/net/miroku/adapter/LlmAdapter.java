package cn.net.miroku.adapter;

import cn.net.miroku.dto.completion.MirokuRequest;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

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
    public abstract Call createChatCompletion(MirokuRequest mirokuRequest);
}
