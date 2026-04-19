package cn.net.miroku.configuration;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpClientConfiguration {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)  // 连接超时（可按需调整）
                .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
                .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时
                // 后续如果要加拦截器、代理、SSL证书等，直接在这里链式追加即可
                .build();
    }
}
