package cn.net.miroku.controller;

import cn.net.miroku.dto.Choice;
import cn.net.miroku.dto.Message;
import cn.net.miroku.dto.chat.completion.DeletedResponse;
import cn.net.miroku.dto.chat.completion.MirokuResponse;
import cn.net.miroku.dto.chat.completion.MirokuRequest;
import cn.net.miroku.service.impl.CompletionServiceImpl;
import cn.net.miroku.tool.JsonUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat/completions")
public class CompletionController {
    private final CompletionServiceImpl chatCompletionService;

    @PostMapping
    public Object create(@RequestBody MirokuRequest mirokuRequest, HttpServletResponse response) throws IOException {
        // 生成本地id
        String id = "Miroku-" + UUID.randomUUID();

        // 调用 LLM 获取响应
        Call tempCall = chatCompletionService.create(mirokuRequest, id);
        okhttp3.Response llmResponse = tempCall.execute();

        // 设置响应码
        response.setStatus(llmResponse.code());

        // 设置响应头
        response.setCharacterEncoding("UTF-8");

        // 根据是否流式返回不同结果
        if (mirokuRequest.getStream() == true) {
            // 流式分支
            response.setContentType("text/event-stream");

            // 返回 StreamingResponseBody，Spring 会异步执行 writeTo
            return (StreamingResponseBody) outputStream -> {
                MirokuResponse resp = new MirokuResponse();
                Choice choice = new Choice();
                StringBuilder ctx = new StringBuilder();
                if (llmResponse.body() != null) {
                    // 如果响应体不为空
                    try (
                            // 获取上游的原始字节流
                            InputStream in = llmResponse.body().byteStream();

                            // 将字节流包装成 BufferedReader 作用：将字节流逐行读取
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    ) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            //System.out.println(line);
                            // 跳过空行
                            if (line.isEmpty()) {
                                outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                                outputStream.flush();
                                continue;
                            }

                            // 跳过非数据行 数据行格式：前六个字符包含data
                            String prefix = line.substring(0, 6);
                            if (!prefix.contains("data")) {
                                outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                                outputStream.write("\n\n".getBytes(StandardCharsets.UTF_8));
                                outputStream.flush();
                                continue;
                            }


                            // 提纯 json 处理 [DONE] 结束标识
                            String jsonData = line.substring(6).trim();
                            if (jsonData.equals("[DONE]")) {
                                // 中转数据给客户端
                                outputStream.write((prefix + "[DONE]\n\n").getBytes(StandardCharsets.UTF_8));
                                outputStream.flush();   // 立即推送 不缓存
                                choice.setFinishReason("stop");
                                break;
                            }

                            JsonNode jsonNode = JsonUtils.toJsonNode(jsonData);
                            if (jsonNode instanceof ObjectNode objectNode) {
                                // 替换 id
                                objectNode.put("id", id);

                                // 转 json 为 字符串 并压缩成一行
                                String sseLines = objectNode.toString();
                                sseLines = prefix + sseLines;

                                // 中转数据给客户端
                                outputStream.write(sseLines.getBytes(StandardCharsets.UTF_8));
                                outputStream.write("\n\n".getBytes(StandardCharsets.UTF_8));
                                outputStream.flush(); // 立即推送 不缓存
                            }

                            // 拼装记录
                            if (resp.getId() == null) {
                                resp.setId(id);
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
                    } catch (IOException e) {
                        if (tempCall.isCanceled()) {
                            choice.setFinishReason("cancel");
                        } else {
                            throw e;
                        }
                    } finally {
                        // 释放链接
                        llmResponse.close();
                        // 组装数据
                        choice.setMessage(new Message("assistant", ctx.toString()));
                        resp.setChoices(List.of(choice));
                        // 保存到数据库
                        chatCompletionService.save(resp);
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
                    resp.setId(id);
                    chatCompletionService.save(resp);
                    return resp;
                }
                return null;
            } catch (IOException e) {
                if (tempCall.isCanceled()) {
                    return null;
                } else {
                    throw e;
                }
            } finally {
                // 释放链接
                llmResponse.close();
            }
        }
    }

    @GetMapping("/{respId}")
    public MirokuResponse select(@PathVariable("respId") String respId) {
        return chatCompletionService.select(respId);
    }

    @DeleteMapping("/{respId}")
    public DeletedResponse delete(@PathVariable("respId") String respId) {
        DeletedResponse deleteResp = new DeletedResponse();
        deleteResp.setId(respId);
        deleteResp.setDeleted(chatCompletionService.delete(respId));
        return deleteResp;
    }

    @PostMapping("/{respId}/cancel")
    public void cancel(@PathVariable("respId") String id) {
        chatCompletionService.cancel(id);
    }
}
