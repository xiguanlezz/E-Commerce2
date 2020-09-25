package com.cj.cn.service;

import com.cj.cn.pojo.User;

public interface IUserService {
    User login(String username, String password);
}
