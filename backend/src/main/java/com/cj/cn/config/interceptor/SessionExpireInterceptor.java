package com.cj.cn.config.interceptor;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class SessionExpireInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotBlank(loginToken)) {
            String userJson = stringRedisTemplate.opsForValue().get(loginToken);
            User user = JsonUtil.jsonToObject(userJson, User.class);
            if (user != null) {
                //重置session的过期时间
                stringRedisTemplate.expire(loginToken, Const.RedisCacheExpireTime.REDIS_SESSION_TIME, TimeUnit.SECONDS);
            }
        }
        return true;
    }
}
