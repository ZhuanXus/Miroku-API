package cn.net.miroku.configuration.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "miroku-api.qwen")
public class Qwen {
    private String baseUrl;
    private String apiKey;
}
