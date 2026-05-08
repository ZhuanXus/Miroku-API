package cn.net.miroku.controller;

import cn.net.miroku.dto.ModelResponse;
import cn.net.miroku.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模型控制器
 */
@RestController
@RequiredArgsConstructor
public class ModelController {
    private final ModelService modelService;

    @GetMapping("/v1/models")
    public ModelResponse getModels() {
        ModelResponse response = new ModelResponse();
        response.setObject("list");
        response.setData(modelService.getModelList());
        return response;
    }
}
