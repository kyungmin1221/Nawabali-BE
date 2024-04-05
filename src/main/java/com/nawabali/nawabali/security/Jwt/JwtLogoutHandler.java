package com.nawabali.nawabali.security.Jwt;

import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.global.tool.redis.RedisTool;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final JwtUtil jwtUtil;
    private final RedisTool redisTool;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        // 쿠키 삭제
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, null);
        response.addCookie(cookie);

        // refresh 토큰 삭제
        String accessToken = jwtUtil.getTokenFromCookieAndName(request, JwtUtil.AUTHORIZATION_HEADER);
        if(StringUtils.hasText(accessToken)){
            accessToken = jwtUtil.substringToken(accessToken);
            String refreshToken = redisTool.getValues(accessToken);
            if(refreshToken.equals("false")){
                throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
            }
            redisTool.deleteValues(accessToken);
        }

        //access의 남은 유효시간만큼  redis에 블랙리스트로 저장
        Long remainedExpiration = jwtUtil.getUserInfoFromToken(accessToken).getExpiration().getTime();
        Long now = new Date().getTime();
        if(remainedExpiration > now){
            Long newExpiration = remainedExpiration - now;
            redisTool.setValues(accessToken, "logout", Duration.ofMillis(newExpiration));
        }
    }
}
