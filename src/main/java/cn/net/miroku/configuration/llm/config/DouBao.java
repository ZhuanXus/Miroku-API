package cn.net.miroku.configuration.llm.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "models.doubao")
public class DouBao {
    private String baseUrl;
    private String apiKey;
}
