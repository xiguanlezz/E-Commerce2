package com.cj.cn.config.interceptor;

import com.cj.cn.common.Const;
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
import java.util.HashMap;
import java.util.Map;

@Order(2)
public class AuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod method = null;
        if (handler instanceof HandlerMethod) {
            method = (HandlerMethod) handler;
        }
        assert method != null;
        String controllerName = method.getBean().getClass().getSimpleName();
        String methodName = method.getMethod().getName();
        String requestURL = httpServletRequest.getRequestURL().toString();  //获得请求的URL

        //如果是登录接口直接放行
//        if (StringUtils.equals(controllerName, "UserManageController") && StringUtils.equals(methodName, "login") && requestURL.contains("login")) {
//            //TODO 转发到登录中心
//            return true;
//        }

        User user = null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(loginToken)) {
            String userJson = stringRedisTemplate.opsForValue().get(loginToken);
            user = JsonUtil.jsonToObject(userJson, User.class);
        }
        if (user == null || (user.getRole() != Const.Role.ROLE_ADMIN)) {
            httpServletResponse.reset();    //这里要添加reset，否则会报异常
            httpServletResponse.setCharacterEncoding("UTF-8");  //这里要设置编码, 否则会乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");   //这里要设置返回值的类型为json

            PrintWriter out = httpServletResponse.getWriter();
            if (user == null) {
                if (StringUtils.equals(controllerName, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    //富文本上传需要单独设置
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("success", false);
                    resultMap.put("msg", "用户未登录, 请登录");
                    out.println(JsonUtil.objectToJson(resultMap));
                } else {
                    out.println(JsonUtil.objectToJson(ResultResponse.error("用户未登录, 请登录")));
                }
            } else {
                if (StringUtils.equals(controllerName, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    //富文本上传需要单独设置
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作, 需要管理员权限");
                    out.println(JsonUtil.objectToJson(resultMap));
                } else {
                    out.println(JsonUtil.objectToJson(ResultResponse.error("无权限操作, 需要管理员权限")));
                }
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }
}
