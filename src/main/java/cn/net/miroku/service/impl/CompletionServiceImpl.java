package cn.net.miroku.service.impl;

import cn.net.miroku.dto.chat.completion.MirokuResponse;
import cn.net.miroku.dto.chat.completion.MirokuRequest;
import cn.net.miroku.adapter.LlmAdapter;
import cn.net.miroku.mapper.ResponseMapper;
import cn.net.miroku.service.CompletionService;
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
public class CompletionServiceImpl implements CompletionService {
    private final List<LlmAdapter> llmStrategies;
    private final ModelService modelService;
    private final ResponseMapper responseMapper;

    @Override
    public Response create(MirokuRequest mirokuRequest) throws IOException {
        // 倘若调用了不存在的模型，则返回 null
        if (modelService.getModels().parallelStream().noneMatch(
                model -> model.getId().equals(mirokuRequest.getModel())
            )) {
            return null;
        }


        for (LlmAdapter strategy : llmStrategies) {
            if (strategy.support(mirokuRequest.getModel())) {
                return strategy.createChatCompletion(mirokuRequest);
            }
        }
        return null;
    }

    @Override
    @Async("dbSaveExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void save(MirokuResponse mirokuResponse) {
        responseMapper.insertResponse(mirokuResponse);
        responseMapper.insertChoices(mirokuResponse);
    }

    @Override
    public MirokuResponse select(String respId) {
        return responseMapper.selectResponse(respId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(String respId) {
        responseMapper.deleteChoices(respId);
        return responseMapper.deleteResponse(respId) == 1;
    }
}
