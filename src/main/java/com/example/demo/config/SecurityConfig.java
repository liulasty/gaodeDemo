package com.example.demo.config;

import com.example.demo.entity.PermissionRule;
import com.example.demo.filter.JwtAuthenticationEntryPoint;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.handler.OAuth2AuthenticationSuccessHandler;
import com.example.demo.mapper.PermissionRuleMapper;
import com.example.demo.service.impl.OAuth2UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
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
    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;
    private final PermissionRuleMapper permissionRuleMapper;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    // 登出处理器
    private final LogoutHandler logoutHandler;

    // 定义常量，避免硬编码
    private static final String AUTH_PATH = "/api/v1/auth/**";
    private static final String SWAGGER_PATH = "/v3/api-docs/**";
    private static final String SWAGGER_UI_PATH = "/doc.html";
    private static final String WEBJARS_PATH = "/webjars/**";
    private static final String FAVICON_PATH = "/favicon.ico";
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
                          JwtDecoder jwtDecoder,
                          PermissionRuleMapper permissionRuleMapper,
                          OAuth2UserServiceImpl oAuth2UserService,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.logoutHandler = logoutHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtDecoder = jwtDecoder;
        this.permissionRuleMapper = permissionRuleMapper;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    }


    @Bean
    @Order(3)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                                "/api/**", // API端点不需要CSRF保护
                                "/oauth2/**" // OAuth2回调不需要CSRF保护
                        )
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 授权配置
                .authorizeHttpRequests(auth -> {
                            List<PermissionRule> rules = permissionRuleMapper.findAllEnabledRules();
                            // 动态注册权限规则
                            rules.forEach(rule -> {
                                RequestMatcher matcher = createRequestMatcher(rule);
                                if (rule.isPublic()) {
                                    auth.requestMatchers(matcher).permitAll(); // 公开访问
                                } else {
                                    auth.requestMatchers(matcher).authenticated(); // 需登录但无角色要求
                                }
                            });
                            auth.requestMatchers(SWAGGER_PATH).permitAll();
                            auth.requestMatchers(SWAGGER_UI_PATH).permitAll();
                            auth.requestMatchers(WEBJARS_PATH).permitAll();
                            auth.requestMatchers(FAVICON_PATH).permitAll();
                            auth.requestMatchers(UPLOAD_PATH).permitAll();
                            auth.requestMatchers(SWAGGER_UI_HTM_PATH).permitAll();
                            auth.requestMatchers("/api/auth/**").permitAll();
                            auth.anyRequest().authenticated();
                        }
                )
                // 会话管理（无状态，因为使用JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(jwtAuthenticationProvider())
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // 认证失败处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // 禁用表单登录，我们使用自定义API登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 开启OAuth2登录，配置自定义userService和successHandler
                .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserService)
                    )
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                // 禁用默认的 OAuth2 资源服务器配置
                .oauth2ResourceServer(AbstractHttpConfigurer::disable)
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


    // 根据规则创建 RequestMatcher（支持Ant路径和HTTP方法）
    private RequestMatcher createRequestMatcher(PermissionRule rule) {
        if (rule.getHttpMethod() == null || rule.getHttpMethod().isEmpty()) {
            return new AntPathRequestMatcher(rule.getPattern());
        } else {
            return new AntPathRequestMatcher(rule.getPattern(), rule.getHttpMethod());
        }
    }


    /**
     * 配置认证管理器bean
     *
     * @return 认证管理器
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {

        // 创建认证提供器列表
        List<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(jwtAuthenticationProvider());
        providers.add(daoAuthenticationProvider());

        return new ProviderManager(providers);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtDecoder);
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
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
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


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 增加迭代次数以提高安全性
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        grantedAuthoritiesConverter.setAuthorityPrefix("");
//        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}