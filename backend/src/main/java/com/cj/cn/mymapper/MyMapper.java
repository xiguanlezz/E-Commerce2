package com.cj.cn.mymapper;

import tk.mybatis.mapper.common.base.select.SelectAllMapper;
import tk.mybatis.mapper.common.example.SelectByExampleMapper;

//自定义实现的Mapper必须和通用Mapper放在不同的包下面
public interface MyMapper<T>
        extends SelectAllMapper<T>, SelectByExampleMapper<T>, MyBatchUpdateMapper<T> {
}
