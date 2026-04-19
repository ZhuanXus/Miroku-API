package cn.net.miroku.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 异步线程池配置 */
@Configuration
public class AsyncWebConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);               // 常驻线程数
        executor.setMaxPoolSize(50);                // 最大线程数
        executor.setQueueCapacity(1000);            // 排队容量
        executor.setThreadNamePrefix("llm-async-"); // 方便看日志
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        configurer.setTaskExecutor(executor);       // 注册给 Spring MVC
        configurer.setDefaultTimeout(120_000L);     // 异步最长等待 120 秒（LLM 生成足够）
    }
}