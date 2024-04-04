package com.nawabali.nawabali.config;

import com.nawabali.nawabali.global.tool.redis.RedisTool;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.security.Jwt.JwtAuthenticationFilter;
import com.nawabali.nawabali.security.Jwt.JwtAuthorizationFilter;
import com.nawabali.nawabali.security.Jwt.JwtUtil;
import com.nawabali.nawabali.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RedisTool redisTool;
    private final UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, redisTool);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, redisTool,userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                        .requestMatchers("/ping").permitAll() // 항상 200 OK 반환하는 health check 전용 API
                        .requestMatchers("/users/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                        .requestMatchers("/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll() // 게시글 상세 조회 허가
                        .requestMatchers("/users/test").permitAll()
                        .requestMatchers("/users/test1").permitAll()
                        .requestMatchers("/api/user/kakao/callback").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api-test").permitAll()
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
