package cn.net.miroku.configuration.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 数据库落库线程池
 */
@Configuration
public class DbPool {
    @Bean("dbSaveExecutor")
    public ThreadPoolTaskExecutor dbSaveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);   // 常驻线程数
        executor.setMaxPoolSize(20);    // 最大线程数
        executor.setQueueCapacity(100);    // 排队容量
        executor.setThreadNamePrefix("llm-db-");    // 线程名前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());    // 拒绝策略
        executor.initialize();  // 初始化

        return executor;
    }
}
