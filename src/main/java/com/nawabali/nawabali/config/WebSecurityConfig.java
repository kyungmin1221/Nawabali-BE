package com.nawabali.nawabali.config;

import com.nawabali.nawabali.global.tool.redis.RedisTool;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.security.Jwt.*;
import com.nawabali.nawabali.security.UserDetailsServiceImpl;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RedisTool redisTool;
    private final UserRepository userRepository;
    private final JwtLogoutHandler jwtLogoutHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, redisTool, userRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, redisTool,userRepository);
    }
    @Bean
    public JwtExceptionHandlerFilter jwtExceptionHandlerFilter(){
        return new JwtExceptionHandlerFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // CORS 관련 설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOrigins(
                    List.of(
                            "http://localhost:3000", "http://localhost:5500", "http://localhost:5000",
                            "http://127.0.0.1:3000", "http://127.0.0.1:5500", "http://127.0.0.1:5000",
                            "http://hhboard.shop", "https://hhboard.shop", "https://vercel-nu-lyart.vercel.app", // 프론트엔드 주소 추가 필요
                            "https://hh-99-nawabali-fe.vercel.app", "https://nawabali-fe.vercel.app",
                            "https://www.dongnaebangnae.com", "https://prod.dongnaebangnae.com",  "https://dongnaebangnae.vercel.app"

                    )
            );
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setMaxAge(3600L);

            configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

            return configuration;
        }));

        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
//        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
//                        .anyRequest().permitAll()
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                                .requestMatchers("/api/user/kakao/callback").permitAll()
                                .requestMatchers("/email-verification").permitAll()
                                .requestMatchers("/ping", "/profile").permitAll() // 항상 200 OK 반환하는 health check 전용 API
                                .requestMatchers(
                                        "/users/logout","/users/signup","/users/check-nickname", "users/info").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                                .requestMatchers("/posts","/posts/district/*", "posts/searchAll", "posts/updateAll").permitAll()
                                .requestMatchers(HttpMethod.GET, "/posts/**").permitAll() // 게시글 상세 조회 허가
                                .requestMatchers("/comments/check/posts/**").permitAll()
//                                .requestMatchers("/swagger/**","/swagger-ui/**","/v3/api-docs/**").permitAll()
                                .requestMatchers("/ws-stomp/**").permitAll()
                                .requestMatchers("/notification/**").permitAll()
                                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

//        http.logout(logoutconfigurer->logoutconfigurer
//                .logoutUrl("/users/logout")
//                .logoutSuccessUrl("/")
//                .addLogoutHandler(jwtLogoutHandler));

        // 필터 관리
        http.addFilterBefore(jwtExceptionHandlerFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
