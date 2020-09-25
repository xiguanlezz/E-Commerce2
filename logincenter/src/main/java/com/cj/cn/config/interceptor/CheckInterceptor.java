package com.cj.cn.config.interceptor;

import com.cj.cn.pojo.User;
import com.cj.cn.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String originalURL = httpServletRequest.getParameter("originalURL");
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        if (StringUtils.isNotBlank(loginToken)) {
            //使用token的方式----------------start---------------------
//                JWTUtil.verify(loginToken);
            //使用token的方式----------------end---------------------

            //使用session的方式----------------start---------------------
            String userJson = stringRedisTemplate.opsForValue().get(loginToken);
            User user = objectMapper.readValue(userJson, User.class);
            assert StringUtils.isNotBlank(originalURL);
            if (user != null) {
                httpServletResponse.reset();    //这里要添加reset，否则会报异常
                httpServletResponse.setCharacterEncoding("UTF-8");  //这里要设置编码, 否则会乱码
                httpServletResponse.sendRedirect(originalURL);
                return false;
            }
            //使用session的方式----------------end---------------------
        }
        return true;
    }
}
