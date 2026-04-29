package cn.net.miroku.controller;

import cn.net.miroku.dto.Choice;
import cn.net.miroku.dto.Message;
import cn.net.miroku.dto.chat.completion.MirokuResponse;
import cn.net.miroku.dto.chat.completion.MirokuRequest;
import cn.net.miroku.service.impl.CompletionServiceImpl;
import cn.net.miroku.tool.JsonUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import tools.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat/completions")
public class CompletionController {
    private final CompletionServiceImpl chatCompletionService;

    @PostMapping
    public Object createChatCompletion(@RequestBody MirokuRequest mirokuRequest, HttpServletResponse response) throws IOException {
        // 调用 LLM 获取响应
        okhttp3.Response llmResponse = chatCompletionService.create(mirokuRequest);

        // 设置响应码
        response.setStatus(llmResponse.code());

        // 根据是否流式返回不同结果
        response.setCharacterEncoding("UTF-8");
        if (mirokuRequest.getStream() == true) {
            // 流式分支
            response.setContentType("text/event-stream");

            // 返回 StreamingResponseBody，Spring 会异步执行 writeTo
            return (StreamingResponseBody) outputStream -> {
                if (llmResponse.body() != null) {
                    // 如果响应体不为空
                    try (
                            // 获取上游的原始字节流
                            InputStream in = llmResponse.body().byteStream();

                            // 将字节流包装成 BufferedReader
                            // 作用：将字节流逐行读取
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    ) {
                        String line;
                        StringBuilder ctx = new StringBuilder();

                        MirokuResponse resp = new MirokuResponse();
                        Choice choice = new Choice();
                        while((line = reader.readLine()) != null) {
                            //System.out.println(line);

                            // 跳过换行
                            if (line.isEmpty()) continue;

                            // 中转数据给客户端
                            outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                            outputStream.write("\n\n".getBytes(StandardCharsets.UTF_8));
                            outputStream.flush(); // 立刻推送数据 不进行缓存

                            // 跳过前六个多余字符 data:  获取响应数据
                            String jsonData = line.substring(6).trim();

                            // 如果遇到结束标志 [DONE] 就停止
                            if (jsonData.equals("[DONE]")) {
                                choice.setFinishReason("stop");
                                choice.setMessage(new Message("assistant", ctx.toString()));
                                resp.setChoices(List.of(choice));
                                break;
                            }

                            // 拼装记录
                            JsonNode jsonNode = JsonUtils.toJsonNode(jsonData);
                            if (resp.getId() == null) {
                                // 设置 response
                                resp.setId(jsonNode.path("id").asString());
                                resp.setObject(jsonNode.path("object").asString());
                                resp.setCreated(jsonNode.path("created").asLong());
                                resp.setModel(jsonNode.path("model").asString());

                                // 设置 choice
                                choice.setIndex(jsonNode
                                        .path("choices")
                                        .path(0).path("index").asInt());
                            }
                            // 拼接回复内容
                            ctx.append(jsonNode.path("choices").path(0)
                                    .path("delta").path("content")
                                    .asString());
                        }

                        // 保存到数据库
                        chatCompletionService.save(resp);
                    } finally {
                        llmResponse.close(); // 释放链接
                    }
                } else {
                    // 如果响应体为空
                }
            };
        } else {
            // 非流式
            response.setContentType("application/json");

            try {
                if (llmResponse.body() != null) {
                    String jsonData = llmResponse.body().string();
                    MirokuResponse resp = JsonUtils.toDto(jsonData, MirokuResponse.class);
                    chatCompletionService.save(resp);
                    return resp;
                }
                return null;
            } finally {
                llmResponse.close(); // 释放链接
            }
        }
    }

    @GetMapping("/{respId}")
    public MirokuResponse select(@PathVariable("respId") String respId) {
        return chatCompletionService.select(respId);
    }
}
