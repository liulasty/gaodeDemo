package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 数据库初始化器
 * 用于应用启动时执行数据库脚本
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Value("${oauth2.init.enabled:true}")
    private boolean initEnabled;
    
    @Bean
    public CommandLineRunner initOAuth2Tables() {
        return args -> {
            if (!initEnabled) {
                log.info("OAuth2数据库初始化已禁用");
                return;
            }
            
            try {
                log.info("开始初始化OAuth2数据库表结构");
                
                // 读取架构脚本
                ClassPathResource schemaResource = new ClassPathResource("db/oauth2_schema.sql");
                String schemaSql = new BufferedReader(
                        new InputStreamReader(schemaResource.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                
                // 执行建表脚本
                String[] schemaBatches = schemaSql.split(";");
                for (String batch : schemaBatches) {
                    if (!batch.trim().isEmpty()) {
                        jdbcTemplate.execute(batch);
                    }
                }
                log.info("OAuth2数据库表结构初始化完成");
                
                // 检查是否已有客户端数据
                Integer clientCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM oauth2_registered_client", Integer.class);
                
                if (clientCount == 0) {
                    log.info("检测到未初始化的OAuth2客户端数据，开始导入默认数据");
                    
                    // 读取数据脚本
                    ClassPathResource dataResource = new ClassPathResource("db/oauth2_data.sql");
                    String dataSql = new BufferedReader(
                            new InputStreamReader(dataResource.getInputStream(), StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    
                    // 执行初始化数据脚本
                    String[] dataBatches = dataSql.split(";");
                    for (String batch : dataBatches) {
                        if (!batch.trim().isEmpty()) {
                            jdbcTemplate.execute(batch);
                        }
                    }
                    log.info("OAuth2客户端数据初始化完成");
                } else {
                    log.info("OAuth2客户端数据已存在，跳过数据初始化");
                }
            } catch (Exception e) {
                log.error("OAuth2数据库初始化失败", e);
                throw e;
            }
        };
    }
} 