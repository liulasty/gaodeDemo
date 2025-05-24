package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户映射器接口，继承自 BaseMapper<User>。
 * 提供基于电子邮件查找用户的功能。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据电子邮件地址查找用户。
     * 
     * @param email 用户的电子邮件地址，不能为空且需符合邮箱格式。
     * @return 包含用户信息的 ScopedValue<User> 对象，如果未找到则返回空值。
     * @throws IllegalArgumentException 如果传入的 email 参数为空或格式不正确。
     */
    @Select("SELECT * FROM t_sys_user WHERE email = #{email}")
    User findByEmail(String email);
}