package cn.net.miroku.adapter;

import cn.net.miroku.configuration.llm.config.Qwen;
import cn.net.miroku.dto.completion.MirokuRequest;
import cn.net.miroku.tool.JsonUtils;
import okhttp3.*;
import org.springframework.stereotype.Component;

/**
 * QWEN 适配器
 */
@Component
public class QwenAdapter extends LlmAdapter {
    private final Qwen properties;

    public QwenAdapter(OkHttpClient okHttpClient, Qwen properties) {
        super(okHttpClient);
        this.properties = properties;
    }

    @Override
    public boolean support(String model) {
        return model.startsWith("qwen");
    }

    @Override
    public Call createChatCompletion(MirokuRequest completionMirokuRequest) {
        // 遍历消息体，把 developer 角色替换为 system 角色 function 角色替换为 tool 角色
        completionMirokuRequest.getMessages().forEach(message -> {
            if ("developer".equals(message.getRole())) {
                message.setRole("system");
            } else if ("function".equals(message.getRole())) {
                message.setRole("tool");
            }
        });

        // 组装请求体
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JsonUtils.toJson(completionMirokuRequest), mediaType);
        Request request = new Request.Builder()
                .url(properties.getBaseUrl() + "/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", completionMirokuRequest.getStream() == true ? "text/event-stream" :"application/json")
                .addHeader("Authorization", "Bearer " + properties.getApiKey())
                .build();

        // 发送请求给 阿里云 服务商 获取响应头
        return okHttpClient.newCall(request);
    }
}
