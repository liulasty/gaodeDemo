package com.example.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.example.demo.util.MyMetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.demo") // 指定Mapper接口扫描路径
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L); // 设置单页最大记录数
        paginationInnerInterceptor.setOverflow(true); // 溢出总页数后是否处理
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 2. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 全局配置
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();

        // 设置主键类型（AUTO-数据库自增，INPUT-自行输入）
        dbConfig.setIdType(IdType.AUTO);

        // 逻辑删除配置（需要实体类有@TableLogic注解）
        dbConfig.setLogicDeleteField("deleted"); // 逻辑删除字段名
        dbConfig.setLogicDeleteValue("1"); // 逻辑已删除值
        dbConfig.setLogicNotDeleteValue("0"); // 逻辑未删除值

        globalConfig.setDbConfig(dbConfig);

        // 设置MetaObjectHandler（自动填充功能）
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());

        return globalConfig;
    }

    /**
     * 自定义MyBatis配置
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 开启驼峰命名转换
            configuration.setMapUnderscoreToCamelCase(true);

            // 枚举类型处理
            configuration.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);

            // 缓存配置
            configuration.setCacheEnabled(true);

            // 其他自定义配置...
        };
    }
}