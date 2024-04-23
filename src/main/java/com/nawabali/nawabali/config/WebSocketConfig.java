package com.nawabali.nawabali.config;

import com.nawabali.nawabali.security.Jwt.JwtUtil;
import com.nawabali.nawabali.security.Jwt.WebSocketJwtInterceptor;
import com.nawabali.nawabali.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 웹 소켓 메세지를 다룰 수 있게 허용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketJwtInterceptor webSocketJwtInterceptor;

    public WebSocketConfig (WebSocketJwtInterceptor webSocketJwtInterceptor) {
        this.webSocketJwtInterceptor = webSocketJwtInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 발행자
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override // 웹소켓 핸드셰이크 커넥션
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel (ChannelRegistration registration) {

        registration.interceptors(webSocketJwtInterceptor);

    }
}
