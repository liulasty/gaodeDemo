package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Token;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author lz
 */
@Mapper
public interface TokenMapper extends BaseMapper<Token> {

    @Select("SELECT * FROM t_sys_token WHERE token = #{token}")
    Token findByToken(String jwt);

    @Update("UPDATE t_sys_token SET expired = #{expired}, revoked = #{revoked} WHERE id = #{id}")
    void updateToken(Token token);
}