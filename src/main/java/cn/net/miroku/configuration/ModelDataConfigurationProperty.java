package cn.net.miroku.configuration;

import cn.net.miroku.dto.ModelData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "models")
public class ModelDataConfigurationProperty {
    private final List<ModelData> modelDataList = new ArrayList<>();
}
