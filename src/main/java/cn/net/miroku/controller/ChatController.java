package cn.net.miroku.controller;

import cn.net.miroku.dto.ChatCompletionRequest;
import cn.net.miroku.dto.ChatCompletionResponse;
import cn.net.miroku.service.ChatCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatCompletionService chatCompletionService;

    @PostMapping("/v1/chat/completions")
    public ChatCompletionResponse createChatCompletion(@RequestBody ChatCompletionRequest request) throws IOException {
        return chatCompletionService.createChatCompletion(request);
    }
}
