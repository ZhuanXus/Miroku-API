package cn.net.miroku.configuration;

import cn.net.miroku.dto.Model;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "miroku-api")
public class ModelListConfiguration {
    private final List<Model> modelList = new ArrayList<>();
}
