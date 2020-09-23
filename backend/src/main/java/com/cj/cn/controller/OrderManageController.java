package com.cj.cn.controller;

import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IOrderService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/order/")
public class OrderManageController {
    @Autowired
    private IOrderService iOrderService;

    @ApiOperation(value = "后台分页查询订单的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台分页查询订单的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "页容量"),
    })
    @GetMapping("list.do")
    public ResultResponse orderList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //权限验证全交给拦截器执行
        return iOrderService.getManageOrderList(pageNum, pageSize);
    }

    @ApiOperation(value = "后台查看订单详情的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台通过订单id查看订单详情")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @GetMapping("detail.do")
    public ResultResponse detail(@RequestParam("orderNo") Long orderNo) {
        //权限验证全交给拦截器执行
        return iOrderService.getManageDetail(orderNo);
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
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //权限验证全交给拦截器执行
        return iOrderService.getManageSearch(orderNo, pageNum, pageSize);
    }

    @ApiOperation(value = "后台发货的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台发货")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @PutMapping("send_goods.do")
    public ResultResponse orderSendGoods(@RequestParam("orderNo") Long orderNo) {
        //权限验证全交给拦截器执行
        return iOrderService.manageSendGoods(orderNo);
    }
}
