package cn.net.miroku.configuration;

import cn.net.miroku.dto.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "miroku-api")
public class ModelDataConfigurationProperty {
    private final List<Model> modelList = new ArrayList<>();
}
