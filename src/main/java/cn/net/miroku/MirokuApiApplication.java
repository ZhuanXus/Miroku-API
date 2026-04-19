package cn.net.miroku;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class MirokuApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MirokuApiApplication.class, args);
    }

}
