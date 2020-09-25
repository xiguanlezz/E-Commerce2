package com.cj.cn.service.impl;

import com.cj.cn.pojo.User;
import com.cj.cn.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class IUserServiceImpl implements IUserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }

        String sql = "SELECT * FROM mmall_user WHERE username=? AND password = ?";
        Object[] params = new Object[]{username, password};
        User result = jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt(1)).setUsername(rs.getString(2)).setPassword(rs.getString(3)).setEmail(rs.getString(4))
                    .setPhone(rs.getString(5)).setQuestion(rs.getString(6)).setAnswer(rs.getString(7)).setRole(rs.getInt(8));
            return user;
        });
        return result;
    }
}
