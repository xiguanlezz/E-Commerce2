package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICartService;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "购物车模块")
@RestController
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "增加一个地址的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;新增一个地址")
    @GetMapping("/list.do")
    public ResultResponse list(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @ApiOperation(value = "往购物车中增加产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;往购物车中增加指定数量的产品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品id", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "要增加的产品数量", paramType = "query")
    })
    @PostMapping("/add.do")
    public ResultResponse add(@RequestParam("productId") Integer productId,
                              @RequestParam("count") Integer count,
                              HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(), productId, count);
    }

    @ApiOperation(value = "更新购物车中产品数量的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;更新购物车中产品的数量为指定值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品id", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "新的产品数量", paramType = "query")
    })
    @PutMapping("/update.do")
    public ResultResponse update(@RequestParam("productId") Integer productId,
                                 @RequestParam("count") Integer count,
                                 HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(), productId, count);
    }

    @ApiOperation(value = "删除购物车中产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;根据传入的id字符串批量删除购物车中的产品")
    @ApiImplicitParam(name = "productIds", value = "产品数组, 用逗号分隔", paramType = "query")
    @DeleteMapping("/delete_product.do")
    public ResultResponse deleteProduct(@RequestParam("productIds") String productIds,
                                        HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(), productIds);
    }

    @ApiOperation(value = "全选用户的购物车中所有产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;全选用户的购物车中的所有产品")
    @PutMapping("/select_all.do")
    public ResultResponse selectAll(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    @ApiOperation(value = "全反选用户的购物车中所有产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;全反选用户的购物车中所有产品的接口")
    @PutMapping("/un_select_all.do")
    public ResultResponse unSelectAll(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @ApiOperation(value = "选中用户购物车中的某种产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;选中用户购物车中的某种产品")
    @ApiImplicitParam(name = "productId", value = "要选中产品的id", paramType = "query")
    @PutMapping("/select.do")
    public ResultResponse select(@RequestParam("productId") Integer productId,
                                 HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @ApiOperation(value = "取消选中用户购物车中的某种产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;取消选中用户购物车中的某种产品")
    @ApiImplicitParam(name = "productId", value = "要取消选中产品的id", paramType = "query")
    @PutMapping("/un_select.do")
    public ResultResponse unSelect(@RequestParam("productId") Integer productId,
                                   HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @ApiOperation(value = "得到用户购物车中产品总数量的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;计算用户购物车中所有产品的总数量")
    @GetMapping("/get_cart_product_count.do")
    public ResultResponse getCartProductCount(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.ok(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
