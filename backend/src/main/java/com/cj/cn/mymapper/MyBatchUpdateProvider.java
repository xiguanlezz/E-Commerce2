package com.cj.cn.mymapper;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

public class MyBatchUpdateProvider extends MapperTemplate {
    public MyBatchUpdateProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    //这个方法就是动态拼接SQL语句
    public String batchUpdate(MappedStatement statement) {
        StringBuilder builder = new StringBuilder();
        builder.append("<foreach collection=\"list\" item=\"user\" separator=\";\">");

        //调用MapperTemplate的方法, 传入MappedStatement即可得到实体类的类名
        Class<?> entityClass = super.getEntityClass(statement);
        //传入实体类的类目即可得到对应的表名
        String tableName = super.tableName(entityClass);

        //update语句头
        String updateClause = SqlHelper.updateTable(entityClass, tableName);
        builder.append(updateClause);

        String idColumn = null;
        String idHolder = null;

        //set更新
        builder.append("<set>");
        Set<EntityColumn> columns = EntityHelper.getColumns(entityClass);
        for (EntityColumn entityColumn : columns) {
            //判断当前字段是否是主键
            boolean isPrimaryKey = entityColumn.isId();
            if (isPrimaryKey) {
                idColumn = entityColumn.getColumn();   //获取表的主键名
                idHolder = entityColumn.getColumnHolder("user");
                continue;
            }
            String column = entityColumn.getColumn();   //获取表的字段名
            String columnHolder = entityColumn.getColumnHolder("user");//对应foreach标签的item属性
            builder.append(column).append("=").append(columnHolder).append(",");
        }
        builder.append("</set>");

        //where条件
        builder.append("where ").append(idColumn).append("=").append(idHolder);

        builder.append("</foreach>");
        return builder.toString();
    }
}
