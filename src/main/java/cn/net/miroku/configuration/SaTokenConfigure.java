package cn.net.miroku.configuration;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    /**注册拦截器*/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // 路由拦截 除了 /login 的路径 检查是否登录，权限是否为user和是否被封禁
                    StpUtil.checkLogin();
                    StpUtil.checkRole("user");
                    StpUtil.checkDisable(StpUtil.getLoginIdAsString());
                }))
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }
}
