package cn.net.miroku.service;

import cn.net.miroku.dto.ModelData;

import java.util.List;

public interface ModelService {
    /**
     * 获取模型列表
     * @return 模型列表
     */
    List<ModelData> getModels();
}
