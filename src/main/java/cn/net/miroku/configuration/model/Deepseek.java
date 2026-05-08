package cn.net.miroku.configuration.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "miroku-api.deepseek")
public class Deepseek {
    private String baseUrl;
    private String apiKey;
}
