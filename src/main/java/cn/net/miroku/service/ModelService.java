package cn.net.miroku.service;

import cn.net.miroku.dto.Model;

import java.util.List;

public interface ModelService {
    /**
     * 获取模型列表
     * @return 模型列表
     */
    List<Model> getModels();
}
