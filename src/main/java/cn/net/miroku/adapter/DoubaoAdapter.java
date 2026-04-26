package cn.net.miroku.adapter;

import cn.net.miroku.dto.chat.completion.Request;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 豆包适配器
 */
@Component
@RequiredArgsConstructor
public class DoubaoAdapter extends LlmAdapter {
    /** http客户端 */
    private final OkHttpClient okHttpClient;

    @Override
    public boolean support(String model) {
        return model.startsWith("doubao");
    }

    @Override
    public Response createChatCompletion(Request request) throws IOException {
        // 遍历消息体，把 developer 角色替换为 system 角色 function 角色替换为 tool 角色
        request.getMessages().forEach(message -> {
            if ("developer".equals(message.getRole())) {
                message.setRole("system");
            } else if ("function".equals(message.getRole())) {
                message.setRole("tool");
            }
        });

        // 组装请求体

        return null;
    }
}
