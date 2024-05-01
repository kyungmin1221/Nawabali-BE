package com.nawabali.nawabali.service;

import com.nawabali.nawabali.controller.NotificationController;
import com.nawabali.nawabali.domain.*;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class NotificationService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationRepository notificationRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Integer> notificationCounts = new HashMap<>();

    public CompletableFuture<SseEmitter> subscribe(User user) {
        Executor executor = Executors.newCachedThreadPool();

        return CompletableFuture.supplyAsync(() -> {
        User users = getUserId(user.getId());
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            Long unreadMessageCount = chatRoomRepository.getUnreadMessageCountsForUser(users.getNickname());
            sseEmitter.send(SseEmitter.event().name("unreadMessageCount").data(unreadMessageCount));
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
            return sseEmitter;
        }

        NotificationController.sseEmitters.put(users.getId(), sseEmitter);
        emitters.put(users.getId(), sseEmitter);

        sseEmitter.onCompletion(()-> {NotificationController.sseEmitters.remove(users.getId());});
        sseEmitter.onTimeout(()-> {NotificationController.sseEmitters.remove(users.getId());});
        sseEmitter.onError((e)-> {NotificationController.sseEmitters.remove(users.getId());});

        return sseEmitter; }, executor);
    }


    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {
        Iterator<Map.Entry<Long, SseEmitter>> iterator = emitters.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, SseEmitter> entry = iterator.next();
            SseEmitter emitter = entry.getValue();

            try { emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (IOException e) {
                emitter.completeWithError(e);
                iterator.remove();
            }
        }
    }

    @Transactional
    public void notifyMessage (Long roomId, String receiver, String sender) {

        User user = getUserNickName(receiver);
        Long userId = user.getId();
        User userSender = getUserNickName(sender);

        Chat.ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));
        List<Chat.ChatMessage> receiveMessageList = chatMessageRepository.findByChatRoomIdOrderByCreatedMessageAtDesc(chatRoom.getId()).orElseThrow(()-> new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));

        Chat.ChatMessage receiveMessage = receiveMessageList.get(0);

        if (NotificationController.sseEmitters.containsKey(userId)) {

            try {
                Notification notification = Notification.builder()
                        .sender(receiveMessage.getSender())
                        .receiver(receiveMessage.getReceiver())
                        .createdAt(receiveMessage.getCreatedMessageAt())
                        .contents(receiveMessage.getMessage())
                        .chatRoom(receiveMessage.getChatRoom())
                        .user(userSender)
                        .build();

                notificationRepository.save(notification);
                notificationCounts.put(userId, notificationCounts.getOrDefault(userId,0) + 1);
            } catch (Exception e) {
                NotificationController.sseEmitters.remove(userId);
            }
        }
    }

    @Transactional
    public void notifyAllMyMessage (String userName) {

        User user = getUserNickName(userName);
        SseEmitter sseEmitter = sseEmitter(user.getId());
        Long unreadMessageCount = chatRoomRepository.getUnreadMessageCountsForUser(userName);

        try {
            sseEmitter.send(SseEmitter.event().name("unreadMessageCount").data(unreadMessageCount));
        } catch (IOException e) {
            log.error("SSE 메시지 전송 중 오류 발생", e);
        }
    }

    @Transactional
    public  void notifyAllYourMessage (String userName) {

        User user = getUserNickName(userName);
        SseEmitter sseEmitter = sseEmitter(user.getId());
        Long unreadMessageCount = chatRoomRepository.getUnreadMessageCountsForUser(userName);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name("unreadMessageCount").data(unreadMessageCount));
            } catch (IOException e) {
                log.error("SSE 메시지 전송 중 오류 발생", e);
            }
        }
    }

    public void deleteAllNotification(User user, Long chatRoomId){

        User users = getUserId(user.getId());
        SseEmitter sseEmitter = sseEmitter(users.getId());

        List<Notification> notifications = notificationRepository.findAllByReceiverAndChatRoomId(user.getNickname(), chatRoomId);
        notificationRepository.deleteAll(notifications);

        if (notificationCounts.containsKey(users.getId())) {notificationCounts.put(users.getId(), 0);}

        if (sseEmitter != null) {
            Integer count = notificationCounts.get(users.getId());
            if (count != null) {
                try {
                    sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(users.getId())));
                } catch (IOException e) {

                }
            }
        }
    }

    public User getUserId (Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public User getUserNickName (String userName){
        User user = userRepository.findByNickname(userName);
        if (user == null) {throw new NullPointerException("해당 회원 정보를 찾을 수 없습니다.: " + userName);}
        return user;
    }

    public SseEmitter sseEmitter (Long userId){
        return NotificationController.sseEmitters.get(userId);
    }
}
