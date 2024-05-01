package com.nawabali.nawabali.global.websocket;

import com.nawabali.nawabali.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketEventListener {

    private final WebSocketChatRoomCount chatRoomCount;
    private final NotificationService notificationService;

    public WebSocketEventListener (WebSocketChatRoomCount chatRoomCount, NotificationService notificationService) {
        this.chatRoomCount = chatRoomCount;
        this.notificationService = notificationService;
    }

    @EventListener
    public void sessionSubscribeEvent (SessionSubscribeEvent subscribeEvent) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(subscribeEvent.getMessage());
        Authentication authentication = (Authentication) subscribeEvent.getMessage().getHeaders().get("simpUser");

        String chatRoomId = null;
        assert  authentication != null;
        String email = authentication.getName();

        String destination = accessor.getDestination();
        if (destination != null) {
            Pattern pattern = Pattern.compile("/sub/chat/room/(\\d+)");
            Matcher matcher = pattern.matcher(destination);
            if (matcher.find()) {
                chatRoomId = matcher.group(1);
            }
        }
        chatRoomCount.addUser(Long.valueOf(chatRoomId), email);
    }

    @EventListener
    public void sessionUnsubscribeEvent (SessionUnsubscribeEvent unsubscribeEvent) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(unsubscribeEvent.getMessage());
        Authentication authentication = (Authentication) unsubscribeEvent.getMessage().getHeaders().get("simpUser");

        Long chatRoomId = null;
        assert  authentication != null;
        String email = authentication.getName();

        List<String> chatRoomIdList = accessor.getNativeHeader("chatRoomId");
        if (chatRoomIdList != null && !chatRoomIdList.isEmpty()) {
            String chatRoomIdString = chatRoomIdList.get(0);
            chatRoomId = Long.valueOf(chatRoomIdString);
        }
       chatRoomCount.outUser(Long.valueOf(chatRoomId),email);
    }
}
