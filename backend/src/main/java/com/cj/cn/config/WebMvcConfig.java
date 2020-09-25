package com.cj.cn.config;

import com.cj.cn.config.interceptor.AuthorityInterceptor;
import com.cj.cn.config.interceptor.LoginInterceptor;
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
                .allowedOrigins("http://www.mmall.com")
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
        //登录拦截器(直接将请求达到登录中心), 因为业务的问题只拦截两个登录请求
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/user/login.do")
                .addPathPatterns("/manage/user/login.do")
                .excludePathPatterns("logout.do")
                .excludePathPatterns("register.do");

        //权限拦截器
        registry.addInterceptor(authorityInterceptor())
                .addPathPatterns("/manage/**")
                .excludePathPatterns("/manage/user/login.do");

        //重置session拦截器
        String[] excludePatterns = new String[]{"/user/login*", "/manage/user/login*", "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                "/api", "/api-docs", "/api-docs/**"};
        registry.addInterceptor(sessionExpireInterceptor())     //不要使用new出来的拦截器, 因为拦截器加载于IOC之前
                .addPathPatterns("/**")
                .excludePathPatterns(excludePatterns);
    }

    @Bean
    public HandlerInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public HandlerInterceptor authorityInterceptor() {
        return new AuthorityInterceptor();
    }


    @Bean
    public HandlerInterceptor sessionExpireInterceptor() {
        return new SessionExpireInterceptor();
    }
}
