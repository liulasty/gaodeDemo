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

/**
 * MyBatis Plus 配置类
 * <p>
 * 该类用于配置 MyBatis-Plus 的相关插件和全局设置，包括分页插件、乐观锁插件、防止全表更新与删除插件等。
 * 同时还设置了全局配置（如主键生成策略、逻辑删除字段等）以及自定义 MyBatis 配置（如驼峰命名转换、枚举类型处理等）。
 *
 * @author lz
 * @date 2025/04/13 16:20:52
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 插件
     * <p>
     * 包含以下插件：
     * <ul>
     *     <li>分页插件：支持分页查询，并限制单页最大记录数。</li>
     *     <li>乐观锁插件：支持版本号控制的乐观锁机制。</li>
     *     <li>防止全表更新与删除插件：防止误操作导致的全表更新或删除。</li>
     * </ul>
     *
     * @return MybatisPlusInterceptor 对象
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
     * 配置 MyBatis-Plus 全局设置
     * <p>
     * 包括以下内容：
     * <ul>
     *     <li>主键生成策略：AUTO 表示数据库自增。</li>
     *     <li>逻辑删除字段及值：指定逻辑删除字段名及对应的已删除和未删除值。</li>
     *     <li>自动填充功能：通过 MetaObjectHandler 实现字段的自动填充。</li>
     * </ul>
     *
     * @return GlobalConfig 对象
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

        // 设置 MetaObjectHandler（自动填充功能）
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());

        return globalConfig;
    }

    /**
     * 自定义 MyBatis 配置
     * <p>
     * 包括以下内容：
     * <ul>
     *     <li>开启驼峰命名转换：将数据库字段名自动映射为驼峰命名的 Java 属性名。</li>
     *     <li>枚举类型处理：使用自定义的 MybatisEnumTypeHandler 处理枚举类型。</li>
     *     <li>缓存配置：启用二级缓存。</li>
     * </ul>
     *
     * @return ConfigurationCustomizer 对象
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