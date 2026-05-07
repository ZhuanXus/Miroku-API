package cn.net.miroku.service.impl;

import cn.net.miroku.configuration.ModelDataConfigurationProperty;
import cn.net.miroku.dto.Model;
import cn.net.miroku.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final ModelDataConfigurationProperty modelDataConfigurationProperty;

    @Override
    public List<Model> getModels() {
        return modelDataConfigurationProperty.getModelList();
    }
}
