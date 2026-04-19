package cn.net.miroku.controller;

import cn.net.miroku.dto.ChatCompletionRequest;
import cn.net.miroku.dto.ChatCompletionResponse;
import cn.net.miroku.service.ChatCompletionService;
import cn.net.miroku.tool.JacksonObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatCompletionService chatCompletionService;
    /** 对象 <--> json */
    private final JacksonObjectMapper jacksonObjectMapper;

    @PostMapping("/v1/chat/completions")
    public Object createChatCompletion(@RequestBody ChatCompletionRequest request, HttpServletResponse response) throws IOException {
        // 调用 LLM 获取响应
        Response llmResponse = chatCompletionService.createChatCompletion(request);

        // 设置响应码
        response.setStatus(llmResponse.code());

        // 根据是否流式返回不同结果
        response.setCharacterEncoding("UTF-8");
        if (request.getStream() == true) {
            // 流式分支
            response.setContentType("text/event-stream");

            // 返回 StreamingResponseBody，Spring 会异步执行 writeTo
            return (StreamingResponseBody) outputStream -> {
                try (InputStream in = llmResponse.body().byteStream()) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                        outputStream.flush(); // ⚠️ 关键：SSE 必须 flush，否则客户端卡住
                    }
                } finally {
                    llmResponse.close(); // 释放链接
                }
            };
        } else {
            // 非流式
            response.setContentType("application/json");

            try {
                String json = llmResponse.body().string();
                return jacksonObjectMapper.fromJson(json, ChatCompletionResponse.class);
            } finally {
                llmResponse.close(); // 释放链接
            }
        }
    }
}
