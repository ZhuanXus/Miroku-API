package cn.net.miroku.service.impl;

import cn.net.miroku.dto.completion.MirokuResponse;
import cn.net.miroku.dto.completion.MirokuRequest;
import cn.net.miroku.adapter.LlmAdapter;
import cn.net.miroku.mapper.ResponseMapper;
import cn.net.miroku.service.CompletionService;
import cn.net.miroku.service.ModelService;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CompletionServiceImpl implements CompletionService {
    private final List<LlmAdapter> llmStrategies;
    private final ModelService modelService;
    private final ResponseMapper responseMapper;
    private final ConcurrentHashMap<String, Call> activeCalls = new ConcurrentHashMap<>(100);

    @Override
    public Call create(MirokuRequest mirokuRequest, String id) {
        // 倘若调用了不存在的模型，则返回 null
        if (modelService.getModelList().parallelStream().noneMatch(
                model -> model.getId().equals(mirokuRequest.getModel())
            )) {
            return null;
        }


        for (LlmAdapter strategy : llmStrategies) {
            if (strategy.support(mirokuRequest.getModel())) {
                Call call = strategy.createChatCompletion(mirokuRequest);
                activeCalls.put(id, call);
                return call;
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

    @Override
    public void cancel(String id) {
        Call call = activeCalls.get(id);
        if (call != null && !call.isCanceled()) {
            // 关闭连接
            call.cancel();
        }
    }
}
