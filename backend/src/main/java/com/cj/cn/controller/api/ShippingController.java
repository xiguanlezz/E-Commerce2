package com.cj.cn.controller.api;

import com.cj.cn.pojo.Shipping;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IShippingService;
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

@Api(tags = "收货地址模块")
@RestController
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShoppingService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "增加一个地址的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;新增一个地址")
    @PostMapping("/add.do")
    public ResultResponse add(Shipping shipping,
                              HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.add(user.getId(), shipping);
    }

    @ApiOperation(value = "删除一个地址的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;根据id删除一个地址")
    @ApiImplicitParam(name = "shippingId", value = "地址id", paramType = "path")
    @DeleteMapping("/{shippingId}")
    public ResultResponse del(@PathVariable("shippingId") Integer shippingId,
                              HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.del(user.getId(), shippingId);
    }

    @ApiOperation(value = "修改地址的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;修改地址的某些信息")
    @PutMapping("/update.do")
    public ResultResponse update(Shipping shipping,
                                 HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.update(user.getId(), shipping);
    }

    @ApiOperation(value = "查询某个地址详细信息的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;根据id查询一个地址的详细信息")
    @ApiImplicitParam(name = "shippingId", value = "地址id", paramType = "path")
    @GetMapping("/{shippingId}")
    public ResultResponse select(@PathVariable("shippingId") Integer shippingId,
                                 HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.select(user.getId(), shippingId);
    }

    @ApiOperation(value = "分页查询地址列表的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;分页查询出地址列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页容量", paramType = "query")
    })
    @GetMapping("/list.do")
    public ResultResponse list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isBlank(loginToken)) {
            return ResultResponse.error("用户未登录");
        }
        String userJson = stringRedisTemplate.opsForValue().get(loginToken);
        User user = JsonUtil.jsonToObject(userJson, User.class);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.list(user.getId(), pageNum, pageSize);
    }
}
