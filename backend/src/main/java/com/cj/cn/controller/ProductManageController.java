package com.cj.cn.controller;

import com.cj.cn.pojo.Product;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IProductService;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.CookieUtil;
import com.cj.cn.util.FastDFSClientUtil;
import com.cj.cn.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "后台产品模块")
@RestController
@RequestMapping("/manage/product/")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private FastDFSClientUtil fastDFSClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "新增产品和更新产品信息的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台新增产品和更新产品信息的统一接口")
    @PostMapping("/save.do")
    public ResultResponse productSave(Product product) {
        //权限验证全交给拦截器执行
        return iProductService.saveOrUpdateProduct(product);
    }

    @ApiOperation(value = "上下架产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台上架下架商品的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品id", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "要设置的产品状态", paramType = "query")
    })
    @PutMapping("/set_sale_status.do")
    public ResultResponse setSaleStatus(@RequestParam("productId") Integer productId,
                                        @RequestParam("status") Integer status) {
        //权限验证全交给拦截器执行
        return iProductService.setSaleStatus(productId, status);
    }

    @ApiOperation(value = "查看商品详情接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台查看商品详情(上架状态、下架状态都可看到)")
    @ApiImplicitParam(name = "productId", value = "商品id", paramType = "path")
    @GetMapping("/{productId}")
    public ResultResponse getDetail(@PathVariable("productId") Integer productId) {
        //权限验证全交给拦截器执行
        return iProductService.getManageProductDetail(productId);
    }

    @ApiOperation(value = "查看商品列表接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台分页查看商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页容量", paramType = "query")
    })
    @GetMapping("/list.do")
    public ResultResponse getList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //权限验证全交给拦截器执行
        return iProductService.getProductList(pageNum, pageSize);

    }

    @ApiOperation(value = "搜索产品接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台分页查看商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "产品名", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品id", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页容量", paramType = "query")
    })
    @GetMapping("/search.do")
    public ResultResponse productSearch(@RequestParam(value = "productName", defaultValue = "") String productName,
                                        @RequestParam(value = "productId", required = false) Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //权限验证全交给拦截器执行
        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }

    @ApiOperation(value = "上传图片接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台上传产品图片到服务器")
    @ApiImplicitParam(name = "upload_file", value = "待上传的图片文件", paramType = "query")
    @PostMapping("upload.do")
    public ResultResponse upload(@RequestParam(value = "upload_file") MultipartFile file) {
        //权限验证全交给拦截器执行
        String path = fastDFSClient.uploadFile(file);
        if ("".equals(path)) {
            return ResultResponse.error("上传文件失败");
        } else {
            return ResultResponse.ok(path);
        }
    }

    @ApiOperation(value = "删除图片接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台上传产品图片到服务器")
    @ApiImplicitParam(name = "upload_file", value = "待上传的图片文件", paramType = "query")
    @DeleteMapping("unUpload.do")
    public ResultResponse unUpload(@RequestParam(value = "filePath") String path,
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

        if (iUserService.checkAdminRole(user).isSuccess()) {
            fastDFSClient.deleteFile(path);
            return ResultResponse.ok();
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "富文本中图片上传接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台富文本中图片上传到服务器")
    @ApiImplicitParam(name = "upload_file", value = "待上传的图片文件", paramType = "query")
    @PostMapping("richtext_img_upload.do")
    public Map<String, Object> richtextImgUpload(@RequestParam(value = "upload_file") MultipartFile file,
                                                 HttpServletResponse httpServletResponse) {
        //权限验证全交给拦截器执行
        Map<String, Object> resultMap = new HashMap<>();
        //富文本中对于返回值有自己的要求, 使用的是simditor需要按照simditor的要求进行返回
        String path = fastDFSClient.uploadFile(file);
        if ("".equals(path)) {
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
        } else {
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", path);
            httpServletResponse.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        }
        return resultMap;
    }
}
