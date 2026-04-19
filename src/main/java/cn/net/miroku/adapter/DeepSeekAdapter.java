package cn.net.miroku.adapter;

import cn.net.miroku.configuration.DeepseekModelConfigurationProperties;
import cn.net.miroku.dto.ChatCompletionRequest;
import cn.net.miroku.dto.ChatCompletionResponse;
import cn.net.miroku.tool.JacksonObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DeepSeekAdapter implements LlmAdapter {
    /** 对象转json工具 */
    private final JacksonObjectMapper jacksonObjectMapper;
    /** http客户端 */
    private final OkHttpClient okHttpClient;
    /** 配置 */
    private final DeepseekModelConfigurationProperties properties;

    @Override
    public boolean support(String model) {
        // 判断是否以模型名称开头
        return model.startsWith("deepseek");
    }

    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest chatCompletionRequest) throws IOException {
        // deepseek 消息没有 develop 角色 替换为 system 角色
        for (int i = 0; i < chatCompletionRequest.getMessages().length; i++) {
            if ("develop".equals(chatCompletionRequest.getMessages()[i].getRole())) {
                chatCompletionRequest.getMessages()[i].setRole("system");
            }
        }

        // 调用 deepseek
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jacksonObjectMapper.toJson(chatCompletionRequest));
        Request request = new Request.Builder()
                .url(properties.getBaseUrl() + "/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + properties.getApiKey())
                .build();

        String responseBody = okHttpClient.newCall(request).execute().body().string();
        return jacksonObjectMapper.fromJson(responseBody, ChatCompletionResponse.class);
    }
}
