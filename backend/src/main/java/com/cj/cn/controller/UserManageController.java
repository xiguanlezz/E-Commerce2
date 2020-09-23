package com.cj.cn.controller;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Api(tags = "后台用户模块")
@RestController
@RequestMapping("/manage/user/")
public class UserManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "登录接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台管理员登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query")
    })
    @PostMapping("/login.do")
    public ResultResponse login(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session,
                                HttpServletResponse httpServletResponse) {
        ResultResponse response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = (User) response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                //将session放到分布式缓存中
                stringRedisTemplate.opsForValue().set(session.getId(), JsonUtil.objectToJson(user), Const.RedisCacheExpireTime.REDIS_SESSION_TIME, TimeUnit.SECONDS);
                CookieUtil.writeLoginToken(httpServletResponse, session.getId());   //将信息写入cookie
                return response;
            } else {
                return ResultResponse.error("不是管理员, 无法登录");
            }
        }
        return response;
    }
}
