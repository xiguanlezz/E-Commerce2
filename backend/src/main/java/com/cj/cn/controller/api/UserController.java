package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.JsonUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "登录接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户登录接口")
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
            //将session放到分布式缓存中
            stringRedisTemplate.opsForValue().set(session.getId(), JsonUtil.objectToJson(user), Const.RedisCacheExpireTime.REDIS_SESSION_TIME, TimeUnit.SECONDS);
            CookieUtil.writeLoginToken(httpServletResponse, session.getId());   //将信息写入cookie
        }
        return response;
    }

    @ApiOperation(value = "退出接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户退出接口")
    @PostMapping("/logout.do")
    public ResultResponse logout(HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(loginToken)) {
            stringRedisTemplate.delete(loginToken); //从分布式缓存中删除session信息
            CookieUtil.delLoginToken(httpServletRequest, httpServletResponse);    //设置cookie有效期为0即删除cookie
        }
        return ResultResponse.ok();
    }

    @ApiOperation(value = "注册接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户注册接口")
    @PostMapping("/register.do")
    public ResultResponse register(User user) {
        return iUserService.register(user);
    }

    @ApiOperation(value = "校验参数合法性接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用于判断用户名或邮箱是否已注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "要校验的参数类型, username表示校验的是用户名, email表示校验的是邮箱", paramType = "query"),
            @ApiImplicitParam(name = "str", value = "要校验的参数值", paramType = "query")
    })
    @PostMapping("/check_valid.do")
    public ResultResponse checkValid(@RequestParam("type") String type,
                                     @RequestParam("str") String str) {
        ResultResponse response = iUserService.checkValid(str, type);
        if (!response.isSuccess()) {
            return ResultResponse.ok();
        }
        return ResultResponse.error(response.getMsg());
    }

    @ApiOperation(value = "登录状态下获取当前用户信息接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台已登录用户获取自己的用户信息")
    @PostMapping("/get_information.do")
    public ResultResponse getUserInfo(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录, 无法获取当前用户的信息");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user != null) {
            return ResultResponse.ok(user);
        }
        return ResultResponse.error("用户未登录, 无法获取当前用户的信息");

    }

    @ApiOperation(value = "获取密保问题接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户根据用户名查询对应的密保问题")
    @ApiImplicitParam(name = "username", value = "用户名", paramType = "query")
    @PostMapping("/forget_get_question.do")
    public ResultResponse forgetGetQuestion(@RequestParam("username") String username) {
        return iUserService.selectQuestion(username);
    }

    @ApiOperation(value = "校验密保问题答案的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台忘记密码中, 判断根据用户名、密保问题和问题答案是否正确匹配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
            @ApiImplicitParam(name = "question", value = "密保问题", paramType = "query"),
            @ApiImplicitParam(name = "answer", value = "密保问题的答案", paramType = "query")
    })
    @RequestMapping("/forget_check_answer.do")
    public ResultResponse forgetCheckAnswer(@RequestParam("username") String username,
                                            @RequestParam("question") String question,
                                            @RequestParam("answer") String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @ApiOperation(value = "根据token重置密码的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台忘记密码中, 根据token直接重置密码(不需要输入原来的密码)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "新密码", paramType = "query"),
            @ApiImplicitParam(name = "forgetToken", value = "忘记密码中密保问题回答正确后返回的token", paramType = "query")
    })
    @PostMapping("/forget_reset_password.do")
    public ResultResponse forgetResetPassword(@RequestParam("username") String username,
                                              @RequestParam("password") String password,
                                              @RequestParam("forgetToken") String forgetToken) {
        return iUserService.forgetResetPassword(username, password, forgetToken);
    }

    @ApiOperation(value = "登录状态下修改密码的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台忘记密码中, 先正确输入原始密码后再设置新密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "passwordOld", value = "原始密码", paramType = "query"),
            @ApiImplicitParam(name = "passwordNew", value = "新密码", paramType = "query"),
    })
    @PostMapping("/reset_password.do")
    public ResultResponse resetPassword(@RequestParam("passwordOld") String passwordOld,
                                        @RequestParam("passwordNew") String passwordNew,
                                        HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录, 无法获取当前用户的信息");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user.getId());
    }

    @ApiOperation(value = "登录状态下更新用户信息的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户中心更新用户基本信息")
    @PostMapping("/update_information.do")
    public ResultResponse updateInformation(User user,
                                            HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User u = JsonUtil.jsonToObject(userJson, User.class);
        if (u == null) {
            return ResultResponse.error("用户未登录");
        }
        user.setId(u.getId());
        return iUserService.updateInformation(user);
    }
}