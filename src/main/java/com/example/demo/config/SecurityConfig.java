package com.example.demo.config;

import com.example.demo.filter.JwtAuthenticationEntryPoint;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.service.DynamicSecurityMetadataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 安全配置
 *
 * @author lz
 * @date 2025/04/13 21:07:57
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JWT认证过滤器
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final DynamicSecurityMetadataSource dynamicSecurityMetadataSource;
    private final DynamicAccessDecisionManager accessDecisionManager;
    // 用户详情服务
    private final UserDetailsService userDetailsService;

    // 登出处理器
    private final LogoutHandler logoutHandler;

    // 定义常量，避免硬编码
    private static final String AUTH_PATH = "/api/v1/auth/**";
    private static final String SWAGGER_PATH = "/v3/api-docs/**";
    private static final String SWAGGER_UI_PATH = "/doc.html";
    ///webjars/**
    private static final String WEBJARS_PATH = "/webjars/**";
    ///favicon.ico
    private static final String FAVICON_PATH = "/favicon.ico";
    ///api/bank-statements/upload
    private static final String UPLOAD_PATH = "/api/bank-statements/upload";
    private static final String SWAGGER_UI_HTM_PATH = "/swagger-ui.html";
    private static final String SWAGGER_RESOURCES_PATH = "/swagger-resources/**";
    private static final String IMAGES_GET_PATH = "/api/images/**";
    private static final String IMAGES_POST_PATH = "/api/images/**";
    private static final String GEOCODE_PATH = "/api/geocode/**";

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService,
                          LogoutHandler logoutHandler,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          DynamicSecurityMetadataSource dynamicSecurityMetadataSource,
                          DynamicAccessDecisionManager accessDecisionManager) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.logoutHandler = logoutHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.dynamicSecurityMetadataSource = dynamicSecurityMetadataSource;
        this.accessDecisionManager = accessDecisionManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // 授权配置
                .authorizeHttpRequests(auth -> auth
                        // 允许未认证访问的端点
                        .requestMatchers(AUTH_PATH, 
                                         SWAGGER_PATH,
                                         SWAGGER_UI_PATH,
                                         WEBJARS_PATH,
                                         FAVICON_PATH,
                                         SWAGGER_RESOURCES_PATH,
                                         SWAGGER_UI_HTM_PATH
                                         ).permitAll()
                        // 图片相关端点权限控制
                        .requestMatchers(HttpMethod.GET, IMAGES_GET_PATH).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, IMAGES_POST_PATH).hasRole("ADMIN")

                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )

                // 添加动态权限决策管理器
                .authorizeHttpRequests(auth -> auth
                        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                            @Override
                            public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                                fsi.setSecurityMetadataSource(dynamicSecurityMetadataSource);
                                fsi.setAccessDecisionManager(accessDecisionManager);
                                return fsi;
                            }
                        })
                )

                // 会话管理（无状态，因为使用JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 认证提供者配置
                .authenticationProvider(daoAuthenticationProvider())

                // 添加JWT过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 认证失败处理
                )

                // 登出配置
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (SecurityContextHolder.getContext() != null) {
                                SecurityContextHolder.clearContext();
                            }
                            response.setStatus(HttpStatus.OK.value());
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 增加迭代次数以提高安全性
        return new BCryptPasswordEncoder(12);
    }

    /**
     * 配置跨域资源共享(CORS)的Bean
     * 该方法定义了允许从哪些域对资源进行请求，以及允许的请求方法和头信息
     * 
     * @return CorsConfigurationSource 用于提供CORS配置的源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 创建一个新的CORS配置
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许来自"http://localhost:3000"的跨域请求
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // 允许的HTTP方法包括GET、POST、PUT、DELETE和OPTIONS
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 限制允许的头字段
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        // 允许携带凭据的请求
        configuration.setAllowCredentials(true);
        
        // 创建一个新的基于URL的CORS配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 将CORS配置注册到所有路径
        source.registerCorsConfiguration("/**", configuration);
        
        // 返回配置源
        return source;
    }

    /**
     * 配置认证入口点 bean
     * 
     * 此方法定义了一个 AuthenticationEntryPoint bean，用于处理未通过认证的请求
     * 它根据异常类型返回更详细的错误信息，以帮助客户端理解认证失败的原因
     * 
     * @return AuthenticationEntryPoint 实现，用于处理认证失败的情况
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            // 设置响应内容类型为 JSON
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            // 设置响应状态码为未授权
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // 根据异常类型返回更详细的错误信息
            if (authException.getMessage().contains("Bad credentials")) {
                // 如果认证失败是由于错误的凭证，返回特定的错误信息
                response.getWriter().write("{ \"error\": \"未授权\", \"message\": \"用户名或密码无效\" }");
            } else {
                // 对于其他认证异常，返回通用的错误信息
                response.getWriter().write("{ \"error\": \"未授权\", \"message\": \"认证失败\" }");
            }
        };
    }

    /**
     * 配置AuthenticationManager为Bean
     * 用于处理认证请求
     * 
     * @param config AuthenticationConfiguration实例，用于获取AuthenticationManager
     * @return AuthenticationManager实例，用于处理认证逻辑
     * @throws Exception 如果处理过程中遇到错误，则抛出异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * 配置DaoAuthenticationProvider为Bean
     * 用于提供基于DAO的认证服务
     * 
     * @return DaoAuthenticationProvider实例，用于配置用户详情服务和密码编码器
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 设置用户详情服务，用于加载用户特定数据
        provider.setUserDetailsService(userDetailsService);
        // 设置密码编码器，用于编码和匹配用户密码
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}