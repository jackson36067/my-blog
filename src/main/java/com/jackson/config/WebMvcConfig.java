package com.jackson.config;

import com.jackson.interceptor.EmployeeLoginInterceptor;
import com.jackson.interceptor.UserLoginInterceptor;
import com.jackson.interceptor.RefreshTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private UserLoginInterceptor userLoginInterceptor;
    @Resource
    private RefreshTokenInterceptor refreshTokenInterceptor;
    @Resource
    private EmployeeLoginInterceptor employeeLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(refreshTokenInterceptor).addPathPatterns("/**").order(0);
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/my-blog/login/code")
                .excludePathPatterns("/user/my-blog/login/password")
                .excludePathPatterns("/user/my-blog/code")
                .excludePathPatterns("/user/my-blog/register")
                .order(1);
        registry.addInterceptor(employeeLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/admin/login");

    }
}
