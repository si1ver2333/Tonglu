package com.cyd.xs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * ⭐ 允许跨域 — 必须加！
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOriginPattern("*"); // 允许所有域
        config.addAllowedHeader("*");        // 允许所有头
        config.addAllowedMethod("*");        // 允许 GET/POST/PUT/DELETE/OPTIONS
        config.setAllowCredentials(true);    // 允许携带 Cookie / Token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * 安全规则配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ⭐ 开启 CORS
                .csrf(csrf -> csrf.disable()) // 关闭 csrf
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ⭐ OPTIONS 必须放行，否则前端永远 403
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 放行首页接口
                        .requestMatchers("/api/v1/home/**").permitAll()

                        // 放行一些不需要认证的接口
                        .requestMatchers("/api/v1/topic/list").permitAll()
                        .requestMatchers("/api/v1/group/list").permitAll()
                        .requestMatchers("/api/v1/expert/resource/list").permitAll()
                        .requestMatchers("/api/v1/expert/list").permitAll()

                        // 放行注册、登录接口
                        .requestMatchers("/api/user/register", "/api/user/login",
                                "/api/user/identity", "/api/user/forgotpassword").permitAll()

                        // 其他接口均需登录
                        .anyRequest().authenticated()
                );

        // 添加 JWT 过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
