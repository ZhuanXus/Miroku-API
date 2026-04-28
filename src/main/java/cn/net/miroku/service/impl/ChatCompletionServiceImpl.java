package cn.net.miroku.service.impl;

import cn.net.miroku.dto.chat.completion.Request;
import cn.net.miroku.adapter.LlmAdapter;
import cn.net.miroku.mapper.ResponseMapper;
import cn.net.miroku.service.ModelService;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatCompletionServiceImpl implements cn.net.miroku.service.ChatCompletionService {
    private final List<LlmAdapter> llmStrategies;
    private final ModelService modelService;
    private final ResponseMapper responseMapper;

    @Override
    public Response createChatCompletion(Request request) throws IOException {
        // 倘若调用了不存在的模型，则返回 null
        if (modelService.getModels().parallelStream().noneMatch(
                model -> model.getId().equals(request.getModel())
            )) {
            return null;
        }


        for (LlmAdapter strategy : llmStrategies) {
            if (strategy.support(request.getModel())) {
                return strategy.createChatCompletion(request);
            }
        }
        return null;
    }

    @Override
    @Async("dbSaveExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void saveChatCompletion(cn.net.miroku.dto.chat.completion.Response response) {
        responseMapper.insertResponse(response);
        responseMapper.insertChoices(response);
    }
}
