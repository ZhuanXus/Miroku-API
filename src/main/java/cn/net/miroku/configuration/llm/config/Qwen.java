package cn.net.miroku.configuration.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "models.qwen")
public class Qwen {
    private String baseUrl;
    private String apiKey;
}
