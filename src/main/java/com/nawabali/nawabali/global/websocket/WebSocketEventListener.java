package com.nawabali.nawabali.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class WebSocketEventListener {

    private final WebSocketChatRoomCount chatRoomCount;

    public WebSocketEventListener (WebSocketChatRoomCount chatRoomCount) {
        this.chatRoomCount = chatRoomCount;
    }

    @EventListener
    public void sessionConnectedEvent (SessionConnectedEvent connectedEvent) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(connectedEvent.getMessage());
        log.info("accessor 리스너" +accessor);
    }

    @EventListener
    public void sessionSubscribeEvent (SessionSubscribeEvent subscribeEvent) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(subscribeEvent.getMessage());
        log.info("accessor 리스너" +accessor);

        Authentication authentication = (Authentication) subscribeEvent.getMessage().getHeaders().get("simpUser");
        log.info("authentication 리스너 " + authentication);
        assert  authentication != null;

        String email = authentication.getName();
        log.info("이메일" + email);

        String chatRoomId = null;
        String destination = accessor.getDestination();
        if (destination != null) {
            // 정규 표현식을 사용하여 "/sub/chat/room/" 다음의 숫자 부분을 추출합니다.
            Pattern pattern = Pattern.compile("/sub/chat/room/(\\d+)");
            Matcher matcher = pattern.matcher(destination);
            if (matcher.find()) {
                chatRoomId = matcher.group(1);
                log.info("제발 방 아이디: " + chatRoomId);
            }
        }

        chatRoomCount.addUser(Long.valueOf(chatRoomId), email);

    }

    @EventListener
    public void sessionUnsubscribeEvent (SessionUnsubscribeEvent unsubscribeEvent) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(unsubscribeEvent.getMessage());
        log.info("accessor 리스너" +accessor);

        Authentication authentication = (Authentication) unsubscribeEvent.getMessage().getHeaders().get("simpUser");
        log.info("authentication 리스너 " + authentication);
        assert  authentication != null;

        String email = authentication.getName();
        log.info("이메일" + email);

        String chatRoomId = null;
        String destination = accessor.getDestination();
        if (destination != null) {
            // 정규 표현식을 사용하여 "/sub/chat/room/" 다음의 숫자 부분을 추출합니다.
            Pattern pattern = Pattern.compile("/sub/chat/room/(\\d+)");
            Matcher matcher = pattern.matcher(destination);
            if (matcher.find()) {
                chatRoomId = matcher.group(1);
                log.info("제발 방 아이디: " + chatRoomId);
            }
        }

       chatRoomCount.outUser(Long.valueOf(chatRoomId),email);
    }

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent disconnectEvent) {
    }

}
