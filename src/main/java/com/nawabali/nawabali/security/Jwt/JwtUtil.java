package com.nawabali.nawabali.security.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.security.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    @Value("${spring.jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Cookie Header 값
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");
    // 토큰 만료 시간
    private final int ACCESS_EXPIRATION_TIME = 60 * 60 * 1000; // 60분
    public final int REFRESH_EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24시간

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 엑세스 토큰생성
    public String createAccessToken(String email, UserRoleEnum role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_EXPIRATION_TIME);
        return BEARER_PREFIX + Jwts.builder()
                .setSubject(email) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                .setIssuedAt(now) // 발급일
                .setExpiration(expireDate) // 만료 시간
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();

    }

    // 카카오 로그인 - 엑세스 토큰 생생 - 권한 필요없음
    public String createAccessToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_EXPIRATION_TIME);

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .setIssuedAt(now) // 발급일
                .setExpiration(expireDate) // 만료 시간
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }


    // 리프레시 토큰 생성
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expireDate)
                .setIssuedAt(now)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // 쿠키(리프레시) 생성
    public Cookie createRefreshCookie(String email) {
        String cookieValue = createRefreshToken(email);
        var refreshTokenCookie = URLEncoder.encode(cookieValue, UTF_8);
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshTokenCookie);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); //Https 접근이 아직 활성화 안됨. 활성화되면 바꿔주기
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_EXPIRATION_TIME);
        return cookie;
    }

    // 쿠키(엑세스) 생성
    public Cookie createAccessCookie(String token) {
        var accessTokenCookie = URLEncoder.encode(token, UTF_8);
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, accessTokenCookie);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(ACCESS_EXPIRATION_TIME);
        cookie.setDomain("dongnaebangnae.com");

        return cookie;
    }


    public String createResponseCookie(String token) {
        String accessToken = URLEncoder.encode(token, UTF_8);
        ResponseCookie cookie = ResponseCookie.from(AUTHORIZATION_HEADER, accessToken)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .maxAge(ACCESS_EXPIRATION_TIME)
                .domain("dongnaebangnae.com")
                .sameSite("None")
                .build();
        return cookie.toString();
    }

    // header 에서 JWT 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    // 토큰 검증
    public boolean validateToken(String token) throws IOException {
        log.info("토큰 검증");
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            throw new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new CustomException(ErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new CustomException(ErrorCode.INVALID_JWT_VALUE);
        }
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getTokenFromCookieAndName(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null; // Cookie not found

    }

    // JWT substring
    public String substringToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer+")) {
            return token.substring(7);
        }
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }


}