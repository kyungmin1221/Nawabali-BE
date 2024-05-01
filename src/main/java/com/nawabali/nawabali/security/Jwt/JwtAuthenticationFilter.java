package com.nawabali.nawabali.security.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.global.tool.redis.RedisTool;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RedisTool redisTool;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisTool redisTool, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/users/login");
        super.setUsernameParameter("email");

        this.redisTool = redisTool;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            UserDto.LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), UserDto.LoginRequestDto.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createAccessToken(username, role);
        log.info("token : " + token);
        Cookie accessCookie = jwtUtil.createAccessCookie(token);

        Cookie refreshCookie = jwtUtil.createRefreshCookie(username);

        log.info("user email : " + username, role);
        log.info("accessCookie value : " + accessCookie.getValue());
        log.info("refreshCookie value : " + refreshCookie.getValue());


//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
//        response.addCookie(accessCookie);
        response.addHeader("Set-Cookie", jwtUtil.createResponseCookie(token));
        // refresh 토큰 redis에 저장
        redisTool.setValues(token.substring(7), refreshCookie.getValue(), Duration.ofMillis(jwtUtil.REFRESH_EXPIRATION_TIME));

        // 로그인 성공 메시지를 JSON 형태로 응답 본문에 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // 로그인 응답 메시지 설정
        User user = userRepository.findByEmail(username).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        Map<String, String> successMessage = new LinkedHashMap<>();
        successMessage.put("message", "회원 로그인에 성공");
        successMessage.put("id", user.getId().toString());
        successMessage.put("nickname", user.getNickname());
        successMessage.put("imgUrl", user.getProfileImage().getImgUrl());
        successMessage.put("district", user.getAddress().getDistrict());

        String jsonResponse = new ObjectMapper().writeValueAsString(successMessage);
        response.getWriter().write(jsonResponse);

        Object principal = authResult.getPrincipal();
        log.info("Principal class: " + (principal == null ? "null" : principal.getClass().getName()));

    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        // 로그인 성공 메시지를 JSON 형태로 응답 본문에 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);

        // 로그인 응답 메시지 설정
        Map<String, String> failedMessage = new LinkedHashMap<>();
        failedMessage.put("status", "401");
        failedMessage.put("errorCode", "USER_NOT_FOUND");
        failedMessage.put("message", "존재하지 않는 회원이거나 아이디 또는 비밀번호가 일치하지 않습니다.");

        String jsonResponse = new ObjectMapper().writeValueAsString(failedMessage);
        response.getWriter().write(jsonResponse);
    }
}
