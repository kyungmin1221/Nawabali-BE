package com.nawabali.nawabali.global.websocket;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketChatRoomCount {
    private final Map<Long, Set<String>> chatRoomUserCount = new ConcurrentHashMap<>();

    public void addUser (Long chatRoomId, String email) {
        chatRoomUserCount.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(email);
    }

    public void outUser (Long chatRoomId, String email) {
        Set<String> Members = chatRoomUserCount.get(chatRoomId);
        if (Members != null) {
            Members.remove(email);
            if (Members.isEmpty()) {
                chatRoomUserCount.remove(chatRoomId);
            }
        }
    }

    public int getChatRoomUserCountInRoom(Long chatroomId) {
        Set <String> users = chatRoomUserCount.get(chatroomId);
        return users != null ? users.size() : 0;
    }
}
