package cn.net.miroku.configuration;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenInterceptor implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {

                    @Override
                    public boolean preHandle(@NonNull HttpServletRequest request,
                                             @NonNull HttpServletResponse response,
                                             @NonNull Object handler) {

                        // 🔑 关键：如果是异步分发 + 已校验标记，直接放行
                        if (request.getDispatcherType() == DispatcherType.ASYNC
                                && request.getAttribute("saTokenChecked") != null) {
                            return true;
                        }

                        // 🔑 正常鉴权逻辑
                        StpUtil.checkLogin();
                        StpUtil.checkRole("user");
                        StpUtil.checkDisable(StpUtil.getLoginIdAsString());

                        // 🔑 打上标记，供异步分发时识别
                        request.setAttribute("saTokenChecked", true);

                        return true;
                    }
                })
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }
}