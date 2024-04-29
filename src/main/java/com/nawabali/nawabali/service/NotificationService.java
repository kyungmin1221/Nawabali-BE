package com.nawabali.nawabali.service;

import com.nawabali.nawabali.controller.NotificationController;
import com.nawabali.nawabali.domain.*;
import com.nawabali.nawabali.dto.NotiDeleteResponseDto;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.nawabali.nawabali.constant.LikeCategoryEnum.LIKE;
import static com.nawabali.nawabali.constant.LikeCategoryEnum.LOCAL_LIKE;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class NotificationService {

    private final ChatRoomRepository chatRoomRepository;
    private final PostRepository postRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    private static Map<Long, Integer> notificationCounts = new HashMap<>();
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // sseEmitter 연결하기
    public SseEmitter subscribe(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 현재 클라이언트를 위한 sseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        Long unreadMessageCount = chatRoomRepository.getUnreadMessageCountsForUser(user.getNickname());
        Map<String,String> eventData = new HashMap<>();

        try {

            eventData.put("contents", "연결 되었습니다.");
            eventData.put("읽지 않은 메세지 수", String.valueOf(unreadMessageCount));

            sseEmitter.send(SseEmitter.event().data(eventData));

        } catch (IOException e) {
            log.error("SSE 연결 에러", e);
            try {
                // 에러 정보를 담은 이벤트 전송
                sseEmitter.send(SseEmitter.event().name("error").data("연결 중 문제가 발생했습니다."));
            } catch (IOException ex) {
                log.error("SSE 에러 메시지 전송 실패", ex);
            } finally {
                sseEmitter.completeWithError(e);
            }
            return sseEmitter; // 에러 상태를 반영하고 SSE Emitter 반환
        }

        // user의 pk값을 key값으로 해서 sseEmitter를 저장
        // 이걸 컨트롤러에 저장하는게 맞는건가...?
        NotificationController.sseEmitters.put(userId, sseEmitter);
        log.info("메세지 알림 연결");
        // user의 pk값을 key값으로 해서 sseEmitter를 저장
        emitters.put(userId, sseEmitter);
        log.info("하트비트 연결");

        sseEmitter.onCompletion(()-> {NotificationController.sseEmitters.remove(userId);  log.info("연결이 종료되었습니다.");});
        sseEmitter.onTimeout(()-> {NotificationController.sseEmitters.remove(userId);  log.info("연결이 타임아웃 되었습니다.");});
        sseEmitter.onError((e)-> {NotificationController.sseEmitters.remove(userId);  log.info("연결이 에러났어요",e);});

        return sseEmitter;
    }


    @Scheduled(fixedRate = 30000) // 매 30초마다 실행
    public void sendHeartbeat() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat")); // 빈 코멘트 이벤트를 보냅니다.
                log.info("하트비트 보내기");
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(userId);
                log.info ("연결안되서 삭제");
            }
        });
    }

    // 채팅 알림
    @Transactional
    public void notifyMessage (String roomNumber, String receiver, String sender) {

        Chat.ChatRoom chatRoom = chatRoomRepository.findByRoomNumber(roomNumber);
        log.info("방번호" + chatRoom);

        User user = userRepository.findByNickname(receiver);
        log.info("알림 받는 사람 : " + user);

        User userSender = userRepository.findByNickname(sender);
        log.info("보낸 사람 : " + userSender);

//        Chat.ChatMessage receiveMessage = (Chat.ChatMessage) chatMessageRepository.findFirstBySenderOrderByCreatedMessageAtDesc(userSender.getNickname())
//                .orElseThrow(()-> new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));

        List<Chat.ChatMessage> receiveMessageList = chatMessageRepository.findByChatRoomIdOrderByCreatedMessageAtDesc(chatRoom.getId());

        if (receiveMessageList.isEmpty()) {
            throw new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND);
        }

        Chat.ChatMessage receiveMessage = receiveMessageList.get(0);
        log.info("메세지 내용?! : " + receiveMessage);

        Long userId = user.getId();
        log.info("유저아이디 잘 들어가?" + userId);



        if (NotificationController.sseEmitters.containsKey(userId)) {

            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);

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

                Map<String,String> eventData = new HashMap<>();
                eventData.put("message", receiveMessage.getSender() + "님이 메시지를 보냈습니다.");
                log.info("message", receiveMessage.getSender() + "님이 메시지를 보냈습니다.");
                eventData.put("notificationId", notification.getId().toString());
                eventData.put("createdAt", receiveMessage.getCreatedMessageAt().toString());
                eventData.put("contents", receiveMessage.getMessage());
                eventData.put("notificationCount", String.valueOf(notificationCounts.get(userId)));

                // JSON 형식의 데이터를 직접 전달
                sseEmitter.send(SseEmitter.event().data(eventData));

//                sseEmitter.send(SseEmitter.event().name("addMessage").data(eventData));

                notificationCounts.put(userId, notificationCounts.getOrDefault(userId,0) + 1);

                sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));

            } catch (Exception e) {
                NotificationController.sseEmitters.remove(userId);
                log.error("Failed to send SSE or save notification", e);
            }
        }
    }

    @Transactional
    public void notifyAllMyMessage (String userName) {

        User user = userRepository.findByNickname(userName);

        SseEmitter sseEmitter = NotificationController.sseEmitters.get(user.getId());
        log.info("본인 " + userName);

        Long unreadMessageCount = chatRoomRepository.getUnreadMessageCountsForUser(userName);
        log.info("본인 " + unreadMessageCount);

        Map<String,String> eventData = new HashMap<>();
        eventData.put("읽지 않은 메세지 수", unreadMessageCount.toString());

        // JSON 형식의 데이터를 직접 전달
        try {
            sseEmitter.send(SseEmitter.event().data(eventData));
        } catch (IOException e) {
            log.error("SSE 메시지 전송 중 오류 발생", e);
        }
    }

    @Transactional
    public  void notifyAllYourMessage (String userName) {

        User user = userRepository.findByNickname(userName);
        log.info("받는 사람 " + userName);

        SseEmitter sseEmitter = NotificationController.sseEmitters.get(user.getId());
        log.info("sseemitter" +sseEmitter);


        Long unreadMessageCount = chatRoomRepository.getUnreadMessageCountsForUser(userName);
        log.info("받는 사람 " + unreadMessageCount);

        Map<String,String> eventData = new HashMap<>();
        eventData.put("읽지 않은 메세지 수", unreadMessageCount.toString());

        // JSON 형식의 데이터를 직접 전달
        try {
            sseEmitter.send(SseEmitter.event().data(eventData));
        } catch (IOException e) {
            log.error("SSE 메시지 전송 중 오류 발생", e);
        }
    }

//    // 댓글 알림
//    @Transactional
//    public void notifyComment (Long postId) {
//
//        // postId로부터 게시물 조회
//        Post post = postRepository.findById(postId)
//                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
//
//        // 해당 게시물에 대한 최신 댓글 조회
//        Comment receiveComment = (Comment) commentRepository.findFirstByPostIdOrderByCreatedAtDesc(postId)
//                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
//
//        // 해당 게시물의 작성자 ID 조회
//        Long userId = post.getUser().getId();
//
//        // 해당 작성자에게 SSEEmitter가 존재하는지 확인
//        if (NotificationController.sseEmitters.containsKey(userId)) {
//
//            // SSEEmitter가 존재한다면 해당 SSEEmitter를 가져옴
//            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
//
//            try {
//                // SSE 이벤트에 담을 데이터 구성
//                Map <String, String> eventData = new HashMap<>();
//
//                eventData.put("message", receiveComment.getUser().getNickname() + "님이 댓글이 달았습니다.");
//                eventData.put("createdAt", receiveComment.getCreatedAt().toString());
//                eventData.put("contents", receiveComment.getContents());
//
//                // SSE 이벤트를 통해 데이터 전송
//                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));
//
//                // Notification 객체 생성 및 저장
//                Notification notification = Notification.builder()
//                        .sender(receiveComment.getUser().getNickname())
//                        .createdAt(receiveComment.getCreatedAt())
//                        .contents(receiveComment.getContents())
//                        .user(post.getUser())
//                        .comment(receiveComment)
//                        .build();
//
//                notificationRepository.save(notification);
//
//                // 알림 개수 증가
//                notificationCounts.put(userId, notificationCounts.getOrDefault(userId,0) + 1);
//
//                // 현재 알림 개수 전송
//                sseEmitter.send(SseEmitter.event().name("notificationCount알림개수").data(notificationCounts.get(userId)));
//
//            } catch (IOException e) {
//
//                // IOException 발생 시 SSEEmitter 제거
//                NotificationController.sseEmitters.remove(userId);
//
//            }
//        }
//    }
//
//    // 좋아요 알림
//    @Transactional
//    public void notifyLike (Long postId, Long userId) {
//
//        // postId로부터 게시물 조회
//        Post post = postRepository.findById(postId)
//                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
//
//        // 해당 게시물에 대한 좋아요 조회
//        Like receiveLike = (Like) likeRepository.findFirstByPostIdAndUserIdAndLikeCategoryEnum(postId, userId, LIKE)
//                .orElseThrow(()-> new CustomException(ErrorCode.LIKE_NOT_FOUND));
//
//        // 해당 게시물의 작성자 ID 조회 (알림 받을 사람)
//        Long postUserId = post.getUser().getId();
//
//        // 해당 작성자에게 SSEEmitter가 존재하는지 확인
//        if (NotificationController.sseEmitters.containsKey(postUserId)) {
//
//            // SSEEmitter가 존재한다면 해당 SSEEmitter를 가져옴
//            SseEmitter sseEmitter = NotificationController.sseEmitters.get(postUserId);
//
//            try {
//                // SSE 이벤트에 담을 데이터 구성
//                Map <String, String> eventData = new HashMap<>();
//
//                eventData.put("message", receiveLike.getUser().getNickname() + "님이 좋아요를 눌렸습니다.");
//                eventData.put("createdAt", String.valueOf(LocalDateTime.now()));
//
//                // SSE 이벤트를 통해 데이터 전송
//                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));
//
//                // Notification 객체 생성 및 저장
//                Notification notification = Notification.builder()
//                        .sender(receiveLike.getUser().getNickname())
//                        .createdAt(LocalDateTime.now())
//                        .user(post.getUser())
//                        .like(receiveLike)
//                        .build();
//
//                notificationRepository.save(notification);
//
//                // 알림 개수 증가
//                notificationCounts.put(postUserId, notificationCounts.getOrDefault(postUserId,0) + 1);
//
//                // 현재 알림 개수 전송
//                sseEmitter.send(SseEmitter.event().name("notificationCount알림개수").data(notificationCounts.get(postUserId)));
//
//            } catch (IOException e) {
//
//                // IOException 발생 시 SSEEmitter 제거
//                NotificationController.sseEmitters.remove(postUserId);
//
//            }
//        }
//    }
//
//    // 로컬 좋아요 알림
//    @Transactional
//    public void notifyLocalLike (Long postId, Long userId) {
//
//        // postId로부터 게시물 조회
//        Post post = postRepository.findById(postId)
//                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
//
//        // 해당 게시물에 대한 좋아요 조회
//        Like receiveLike = (Like) likeRepository.findFirstByPostIdAndUserIdAndLikeCategoryEnum(postId, userId, LOCAL_LIKE)
//                .orElseThrow(()-> new CustomException(ErrorCode.LIKE_NOT_FOUND));
//
//        // 해당 게시물의 작성자 ID 조회 (알림 받을 사람)
//        Long postUserId = post.getUser().getId();
//
//        // 해당 작성자에게 SSEEmitter가 존재하는지 확인
//        if (NotificationController.sseEmitters.containsKey(postUserId)) {
//
//            // SSEEmitter가 존재한다면 해당 SSEEmitter를 가져옴
//            SseEmitter sseEmitter = NotificationController.sseEmitters.get(postUserId);
//
//            try {
//                // SSE 이벤트에 담을 데이터 구성
//                Map <String, String> eventData = new HashMap<>();
//
//                eventData.put("message", receiveLike.getUser().getNickname() + "님이 주민 추천 좋아요를 눌렸습니다.");
//                eventData.put("createdAt", String.valueOf(LocalDateTime.now()));
//
//                // SSE 이벤트를 통해 데이터 전송
//                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));
//
//                // Notification 객체 생성 및 저장
//
//                Notification notification = Notification.builder()
//                        .sender(receiveLike.getUser().getNickname())
//                        .createdAt(LocalDateTime.now())
//                        .user(post.getUser())
//                        .like(receiveLike)
//                        .build();
//
//                notificationRepository.save(notification);
//
//                // 알림 개수 증가
//                notificationCounts.put(postUserId, notificationCounts.getOrDefault(postUserId,0) + 1);
//
//                // 현재 알림 개수 전송
//                sseEmitter.send(SseEmitter.event().name("notificationCount알림개수").data(notificationCounts.get(postUserId)));
//
//            } catch (IOException e) {
//
//                // IOException 발생 시 SSEEmitter 제거
//                NotificationController.sseEmitters.remove(postUserId);
//
//            }
//        }
//    }

    // 해당 알림 삭제
    public NotiDeleteResponseDto deleteNotification (Long notificationId) throws IOException {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        Long userId = notification.getUser().getId();

        notificationRepository.delete(notification);

        // 알림 개수 감소
        if (notificationCounts.containsKey(userId)) {

            int currentCount = notificationCounts.get(userId);

            if (currentCount > 0) {
                notificationCounts.put(userId, currentCount - 1);
            }
        }

        // 현재 알림 개수 전송
        SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
        sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));

        return new NotiDeleteResponseDto("알림이 삭제되었습니다.");
    }

    public NotiDeleteResponseDto deleteAllNotification(User user) throws IOException {

        User users = userRepository.findById(user.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        log.info("유저 잘 들어와?" + users);
        log.info("유저 아이디 확인" + user.getId());

        List<Notification> notifications = notificationRepository.findAllByReceiver(user.getNickname())
                .orElseThrow(()-> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        log.info("Db 들어와?" +notifications);

        notificationRepository.deleteAll(notifications);
        log.info("Db 삭제" +notifications);

        if (notificationCounts.containsKey(users.getId())) {
            notificationCounts.put(users.getId(), 0);
        }

        SseEmitter sseEmitter = NotificationController.sseEmitters.get(users.getId());
        sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(users.getId())));

        return new NotiDeleteResponseDto("모든 알림이 삭제되었습니다.");
    }
}
