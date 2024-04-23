package com.nawabali.nawabali.security.Jwt;

import co.elastic.clients.elasticsearch.security.get_token.AuthenticatedUser;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.nawabali.nawabali.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketJwtInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketJwtInterceptor(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor headerAccessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        // CONNECT 프레임인지 확인
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {

            // Authorization 헤더가 있는지 확인
            List<String> authorizationHeader = headerAccessor.getNativeHeader("Authorization");

            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {

                String token = authorizationHeader.get(0); // 첫 번째 Authorization 헤더 값 사용
                log.info("토큰" +token);

                String accessToken = token.substring(JwtUtil.BEARER_PREFIX.length());
                log.info("엑세스토큰" +accessToken);
                // token 사용

                Claims claims = jwtUtil.getUserInfoFromToken(accessToken);
                log.info("클레임" +claims);

                UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get("sub").toString());
                log.info("userdetails :" + userDetails);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                log.info("authentication :"+authentication);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                headerAccessor.setUser(authentication);

                return message;
            } else {
                throw new RuntimeException(new AuthException("연결 실패: 토큰 유효하지 않음"));
            }

        }

        return message;
    }

}
