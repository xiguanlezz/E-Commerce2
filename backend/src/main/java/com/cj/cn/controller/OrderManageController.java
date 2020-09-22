package com.cj.cn.controller;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IOrderService;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.JsonUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/order/")
public class OrderManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "后台分页查询订单的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台分页查询订单的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "页容量"),
    })
    @GetMapping("list.do")
    public ResultResponse orderList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                    HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录, 请登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.getManageOrderList(pageNum, pageSize);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "后台查看订单详情的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台通过订单id查看订单详情")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @GetMapping("detail.do")
    public ResultResponse detail(@RequestParam("orderNo") Long orderNo,
                                 HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录, 请登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.getManageDetail(orderNo);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "后台搜索订单的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台通过订单号分页搜索订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号"),
            @ApiImplicitParam(name = "pageNum", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "页容量")
    })
    @GetMapping("search.do")
    public ResultResponse search(@RequestParam("orderNo") Long orderNo,
                                 @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录, 请登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.getManageSearch(orderNo, pageNum, pageSize);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "后台发货的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台发货")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @PutMapping("send_goods.do")
    public ResultResponse orderSendGoods(@RequestParam("orderNo") Long orderNo,
                                         HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录, 请登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }
}
