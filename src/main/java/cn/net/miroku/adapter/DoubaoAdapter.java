package cn.net.miroku.adapter;

import cn.net.miroku.configuration.llm.config.DouBao;
import cn.net.miroku.dto.chat.completion.Request;
import cn.net.miroku.tool.JsonUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 豆包适配器
 */
@Component
public class DoubaoAdapter extends LlmAdapter {
    private final DouBao properties;

    public DoubaoAdapter(OkHttpClient okHttpClient, DouBao properties) {
        super(okHttpClient);
        this.properties = properties;
    }

    @Override
    public boolean support(String model) {
        return model.startsWith("doubao");
    }

    @Override
    public Response createChatCompletion(Request completionRequest) throws IOException {
        // 遍历消息体，把 developer 角色替换为 system 角色 function 角色替换为 tool 角色
        completionRequest.getMessages().forEach(message -> {
            if ("developer".equals(message.getRole())) {
                message.setRole("system");
            } else if ("function".equals(message.getRole())) {
                message.setRole("tool");
            }
        });

        // 组装请求体
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JsonUtils.toJson(completionRequest), mediaType);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(properties.getBaseUrl() + "chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", completionRequest.getStream() == true ? "text/event-stream" :"application/json")
                .addHeader("Authorization", "Bearer " + properties.getApiKey())
                .build();

        // 发送请求给 doubao 服务商 获取响应头
        return okHttpClient.newCall(request).execute();
    }
}
