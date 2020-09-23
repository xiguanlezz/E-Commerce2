package com.cj.cn.config.interceptor;

import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Order(-1)
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String requestURI = httpServletRequest.getRequestURI();     //获得请求的URI
        //如果请求的URI中包含login, 直接放行
        if (requestURI.contains("login") || requestURI.contains("register")) {
            return true;
        }

//        HandlerMethod method = (HandlerMethod) handler;
//        assert method != null;
//        String methodName = method.getMethod().getName();
//        if (StringUtils.equals(methodName, "updateInformation") || StringUtils.equals(methodName, "resetPassword") || StringUtils.equals(methodName, "getUserInfo")) {
//            return true;    //特殊方法, Controller层进行校验
//        }

        User user = null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(loginToken)) {
            String userJson = stringRedisTemplate.opsForValue().get(loginToken);
            user = JsonUtil.jsonToObject(userJson, User.class);
        }

        if (user == null) {
            httpServletResponse.reset();    //这里要添加reset，否则会报异常
            httpServletResponse.setCharacterEncoding("UTF-8");  //这里要设置编码, 否则会乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");   //这里要设置返回值的类型为json

            PrintWriter out = httpServletResponse.getWriter();
            out.println(JsonUtil.objectToJson(ResultResponse.error("用户未登录, 请登录")));
            out.flush();
            out.close();
            return false;
        }
        return true;
    }
}
