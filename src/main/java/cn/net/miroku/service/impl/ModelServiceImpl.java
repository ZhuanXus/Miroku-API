package cn.net.miroku.service.impl;

import cn.net.miroku.configuration.ModelDataConfigurationProperty;
import cn.net.miroku.dto.ModelData;
import cn.net.miroku.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final ModelDataConfigurationProperty modelDataConfigurationProperty;

    @Override
    public List<ModelData> getModels() {
        return modelDataConfigurationProperty.getModelDataList();
    }
}
