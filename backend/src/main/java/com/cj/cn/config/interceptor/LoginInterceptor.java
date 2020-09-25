package com.cj.cn.config.interceptor;

import com.cj.cn.pojo.User;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.JWTUtil;
import com.cj.cn.util.JsonUtil;
import com.cj.cn.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(1)
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String requestURL = httpServletRequest.getRequestURL().toString();

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            //判断是否是cas之后的请求即请求中是否带有ticket
            String ticket = httpServletRequest.getParameter("ticket");
            if (StringUtils.isNotBlank(ticket)) {
                //使用token的方式----------------start---------------------
//                JWTUtil.verify(ticket);
                //使用token的方式----------------end---------------------

                //使用session的方式----------------start---------------------
                String userJson = stringRedisTemplate.opsForValue().get(ticket);
                User user = JsonUtil.jsonToObject(userJson, User.class);
                if (user != null) {
                    CookieUtil.writeLoginToken(httpServletResponse, ticket);
                    //写完cookie后原地跳转登录请求
                    httpServletResponse.sendRedirect(PropertiesUtil.getProperty("login.url", "http://www.mmall.com:8080/user/login.do"));
                    return false;
                }
                //使用session的方式----------------end---------------------
            }
        } else {
            //判断cookie信息是否有效

            //使用token的方式----------------start---------------------
//            JWTUtil.verify(loginToken);
            //使用token的方式----------------end---------------------

            //使用session的方式----------------start---------------------
            String userJson = stringRedisTemplate.opsForValue().get(loginToken);
            User user = JsonUtil.jsonToObject(userJson, User.class);
            if (user != null) {
                //MVVM架构中都是前端路由, 后端只负责提供数据
                httpServletResponse.sendRedirect(PropertiesUtil.getProperty("index.url", "http://www.mmall.com"));
                return false;
            }
            //使用session的方式----------------end---------------------
        }
        if (requestURL.contains("login")) {
            httpServletResponse.reset();    //这里要添加reset，否则会报异常
            httpServletResponse.setCharacterEncoding("UTF-8");  //这里要设置编码, 否则会乱码
            String loginCenterURL = PropertiesUtil.getProperty("login.center.url", "http://login.mmall.com:8888/check");
            httpServletResponse.sendRedirect(loginCenterURL + "?originalURL=" + PropertiesUtil.getProperty("login.url", "http://www.mmall.com:8080/user/login.do"));
        }
        return false;
    }
}
