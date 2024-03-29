package com.nawabali.nawabali.security.Jwt;

import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.global.tool.redis.RedisTool;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisTool redisTool;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getTokenFromCookieAndName(req, JwtUtil.AUTHORIZATION_HEADER);
        log.info("accessToken : "+ accessToken);
        if(StringUtils.hasText(accessToken)){
            // 토큰 유무 확인
            accessToken = jwtUtil.substringToken(accessToken);
            String refreshToken =redisTool.getValues(accessToken);
            log.info("저장된 refreshToken :" + refreshToken);

            if(!jwtUtil.validateToken(accessToken)){
                if(StringUtils.hasText(refreshToken)){
                    log.info("refresh 토큰 존재. accessToken 재발급 진행");

                    Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
                    String email = info.getSubject();
                    User user = userRepository.findByEmail(email);
                    if(user==null){
                        throw new CustomException(ErrorCode.USER_NOT_FOUND);
                    }
                    UserRoleEnum role = user.getRole();
                    // 새로운 access, refresh Token 발행
                    String newAccessToken = jwtUtil.createAccessToken(email, role);
                    Cookie newAccessCookie = jwtUtil.createAccessCookie(newAccessToken);
                    String newRefreshToken = jwtUtil.createRefreshToken(email);
                    log.info("발급한 유저의 email : " + email);

                    res.addCookie(newAccessCookie);

                    redisTool.deleteValues(accessToken);
                    log.info("기존 refreshToken 삭제 key :" + accessToken );
                    redisTool.setValues(jwtUtil.substringToken(newAccessToken), newRefreshToken, Duration.ofMillis(jwtUtil.REFRESH_EXPIRATION_TIME));
                    log.info("refreshToken 재발급 완료 key : " + jwtUtil.substringToken(newAccessToken));

                    try{
                        setAuthentication(info.getSubject());
                    } catch(Exception e){
                        log.error(e.getMessage());
                        return;
                    }
                }else{
                    log.error("토큰 에러.");
                    return;
                }
            }
            else{
                Claims info = jwtUtil.getUserInfoFromToken(accessToken);

                try{
                    setAuthentication(info.getSubject());
                } catch(Exception e){
                    log.error(e.getMessage());
                    return;
                }
            }

        }
        filterChain.doFilter(req, res);

    }

    public void setAuthentication(String email){
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
        log.info(email + " 인증 완료");
    }

    private Authentication createAuthentication(String email){
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
