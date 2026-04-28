package cn.net.miroku;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync    // 启用异步
@SpringBootApplication
public class MirokuApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MirokuApiApplication.class, args);
    }

}
