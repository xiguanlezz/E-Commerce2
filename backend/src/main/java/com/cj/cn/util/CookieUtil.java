package com.cj.cn.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = "mmall.com";   //只有下一级域名能读到cookie
    private final static String COOKIE_NAME = "mmall_login_token";

    //读取请求中的cookie信息并进行校验
    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                log.info("read cookieName: {}, cookieValue: {}", ck.getName(), ck.getValue());
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    //将用户信息放入cookie中并添加到响应中
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");    //代表设置在根目录

        //单位是秒
        ck.setMaxAge(60 * 60 * 24 * 365);  //如果是-1, 代表永久
        log.info("write cookieName: {}, cookieValue: {}", ck.getName(), ck.getValue());
        response.addCookie(ck);
    }

    //读取请求中的cookie信息, 设置为失效后添加到响应中
    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);
                    log.info("del cookieName: {}, cookieValue: {}", ck.getName(), ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
