package com.cj.cn.controller;

import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("/check")
    public String check(Model model,
                        @RequestParam(value = "originalURL", required = false) String originalURL) {
        model.addAttribute("originalURL", originalURL);
        return "login";
    }

    @ResponseBody
    @RequestMapping("/login")
    public ResultResponse login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                @RequestParam(value = "username", required = false) String username,
                                @RequestParam(value = "password", required = false) String password,
                                @RequestParam(value = "originalURL", required = false) String originalURL) throws IOException {
        User user = iUserService.login(username, password);
        if (user == null) {
            return ResultResponse.error("账号或者密码错误");
        }

        //使用session的方式----------------start---------------------
        String token = httpServletRequest.getSession().getId();     //使用session的方式
        CookieUtil.writeLoginToken(httpServletResponse, token);
        stringRedisTemplate.opsForValue().set(token, objectMapper.writeValueAsString(user), 30, TimeUnit.MINUTES);
        //使用session的方式----------------end---------------------

        //使用token的方式----------------start---------------------
//                String token = JWTUtil.getToken(user);     //使用JWT生成token的方式
        //使用token的方式----------------end---------------------

        //登录中心认证成功后带上Redis中的key重定向到原始的登录接口
        httpServletResponse.sendRedirect(originalURL + "?ticket=" + token);
        return ResultResponse.ok();
    }
}
