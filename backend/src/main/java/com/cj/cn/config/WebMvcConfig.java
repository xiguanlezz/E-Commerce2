package com.cj.cn.config;

import com.cj.cn.config.interceptor.SessionExpireInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Order(value = -1)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("-----------CORS---------------");
        registry.addMapping("/**")
                .allowCredentials(true)
                //http默认端口是80, https默认端口是443
                .allowedOrigins("http://127.0.0.1", "http://localhost")
                .allowedOrigins("http://127.0.0.1:8088", "http://localhost:8088")
                .allowedOrigins("http://127.0.0.1:8086", "http://localhost:8086")
                .allowedMethods("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionExpireInterceptor())     //不要使用new出来的拦截器, 因为拦截器加载于IOC之前
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login*", "/manage/user/login*");
    }

    @Bean
    public HandlerInterceptor sessionExpireInterceptor() {
        return new SessionExpireInterceptor();
    }
}
