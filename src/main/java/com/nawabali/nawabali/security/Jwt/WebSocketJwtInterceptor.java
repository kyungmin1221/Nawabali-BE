package com.nawabali.nawabali.security.Jwt;


import com.nawabali.nawabali.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

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

        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {

            List<String> authorizationHeader = headerAccessor.getNativeHeader("Authorization");

            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {

                String token = authorizationHeader.get(0);
                String accessToken = token.substring(JwtUtil.BEARER_PREFIX.length());

                Claims claims = jwtUtil.getUserInfoFromToken(accessToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get("sub").toString());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

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
