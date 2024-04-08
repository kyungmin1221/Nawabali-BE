package com.nawabali.nawabali.service;

import com.nawabali.nawabali.controller.NotificationController;
import com.nawabali.nawabali.domain.*;
import com.nawabali.nawabali.dto.NotiDeleteResponseDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.nawabali.nawabali.constant.LikeCategoryEnum.LIKE;
import static com.nawabali.nawabali.constant.LikeCategoryEnum.LOCAL_LIKE;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ChatRoomRepository chatRoomRepository;
    private final PostRepository postRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private static Map<Long, Integer> notificationCounts = new HashMap<>();

    // sseEmitter 연결하기
    public SseEmitter subscribe(Long userId) {

        // 현재 클라이언트를 위한 sseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            // 연결하기
            sseEmitter.send(SseEmitter.event().name("연결 되었습니다."));
        } catch (IOException e) {
            e.printStackTrace();
        /*
        여기서 catch 블록은 IOException이 발생했을 때 실행되는 부분입니다.
        e.printStackTrace()는 예외의 추적(trace) 정보를 출력하는 메서드입니다.
        이렇게 하면 예외가 발생했을 때 해당 예외의 발생 경로를 확인할 수 있습니다.
        하지만 이는 단순히 예외를 처리하고, 해당 예외를 더 이상 상위로 전파하지 않고 그대로 종료시키는 방식입니다.
         */
        }

        // user의 pk값을 key값으로 해서 sseEmitter를 저장
        // 이걸 컨트롤러에 저장하는게 맞는건가...?
        NotificationController.sseEmitters.put(userId, sseEmitter);

        sseEmitter.onCompletion(()-> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onTimeout(()-> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onError((e)-> NotificationController.sseEmitters.remove(userId));

        return sseEmitter;
    }

    // 채팅 알림
    @Transactional
    public void notifyMessage (String roomNumber, Long receiver, String sender) {

        Chat.ChatRoom chatRoom = chatRoomRepository.findByRoomNumber(roomNumber);

        User user = userRepository.findById(receiver)
                .orElseThrow(()-> new CustomException(ErrorCode.FORBIDDEN_CHATMESSAGE));

        User userSender = userRepository.findByNickname(sender);

        Chat.ChatMessage receiveMessage = (Chat.ChatMessage) chatMessageRepository.findFirstBySenderOrderByCreatedAtDesc(userSender.getNickname())
                .orElseThrow(()-> new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));

        Long userId = user.getId();

        if (NotificationController.sseEmitters.containsKey(userId)) {

            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);

            try {
                Map<String,String> eventData = new HashMap<>();
                eventData.put("message", receiveMessage.getSender() + "님이 메시지를 보냈습니다.");
                eventData.put("createdAt", receiveMessage.getCreatedAt().toString());
                eventData.put("contents", receiveMessage.getMessage());

                sseEmitter.send(SseEmitter.event().name("addMessage알림").data(eventData));

                Notification notification = Notification.builder()
                        .sender(receiveMessage.getSender())
                        .createdAt(receiveMessage.getCreatedAt())
                        .contents(receiveMessage.getMessage())
                        .chatRoom(receiveMessage.getChatRoom())
                        .build();

                notificationRepository.save(notification);

                notificationCounts.put(userId, notificationCounts.getOrDefault(userId,0) + 1);

                sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));

            } catch (Exception e) {
                NotificationController.sseEmitters.remove(userId);
            }
        }
    }

    // 댓글 알림
    @Transactional
    public void notifyComment (Long postId) {

        // postId로부터 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 해당 게시물에 대한 최신 댓글 조회
        Comment receiveComment = (Comment) commentRepository.findFirstByPostIdOrderByCreatedAtDesc(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 해당 게시물의 작성자 ID 조회
        Long userId = post.getUser().getId();

        // 해당 작성자에게 SSEEmitter가 존재하는지 확인
        if (NotificationController.sseEmitters.containsKey(userId)) {

            // SSEEmitter가 존재한다면 해당 SSEEmitter를 가져옴
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);

            try {
                // SSE 이벤트에 담을 데이터 구성
                Map <String, String> eventData = new HashMap<>();

                eventData.put("message", receiveComment.getUser().getNickname() + "님이 댓글이 달았습니다.");
//                eventData.put("sender", receiveComment.getUser().getNickname());
                eventData.put("createdAt", receiveComment.getCreatedAt().toString());
                eventData.put("contents", receiveComment.getContents());

                // SSE 이벤트를 통해 데이터 전송
                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                // Notification 객체 생성 및 저장


                Notification notification = Notification.builder()
                        .sender(receiveComment.getUser().getNickname())
                        .createdAt(receiveComment.getCreatedAt())
                        .contents(receiveComment.getContents())
                        .user(post.getUser())
//                        .post(post)
                        .comment(receiveComment)
                        .build();

                notificationRepository.save(notification);

                // 알림 개수 증가
                notificationCounts.put(userId, notificationCounts.getOrDefault(userId,0) + 1);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount알림개수").data(notificationCounts.get(userId)));

            } catch (IOException e) {

                // IOException 발생 시 SSEEmitter 제거
                NotificationController.sseEmitters.remove(userId);

            }
        }
    }

    // 좋아요 알림
    @Transactional
    public void notifyLike (Long postId, Long userId) {

        // postId로부터 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 해당 게시물에 대한 좋아요 조회
        Like receiveLike = (Like) likeRepository.findFirstByPostIdAndUserIdAndLikeCategoryEnum(postId, userId, LIKE)
                .orElseThrow(()-> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        // 해당 게시물의 작성자 ID 조회 (알림 받을 사람)
        Long postUserId = post.getUser().getId();

        // 해당 작성자에게 SSEEmitter가 존재하는지 확인
        if (NotificationController.sseEmitters.containsKey(postUserId)) {

            // SSEEmitter가 존재한다면 해당 SSEEmitter를 가져옴
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(postUserId);

            try {
                // SSE 이벤트에 담을 데이터 구성
                Map <String, String> eventData = new HashMap<>();

                eventData.put("message", receiveLike.getUser().getNickname() + "님이 좋아요를 눌렸습니다.");
                eventData.put("createdAt", String.valueOf(LocalDateTime.now()));

                // SSE 이벤트를 통해 데이터 전송
                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                // Notification 객체 생성 및 저장
                Notification notification = Notification.builder()
                        .sender(receiveLike.getUser().getNickname())
                        .createdAt(LocalDateTime.now())
                        .user(post.getUser())
                        .like(receiveLike)
                        .build();

                notificationRepository.save(notification);

                // 알림 개수 증가
                notificationCounts.put(postUserId, notificationCounts.getOrDefault(postUserId,0) + 1);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount알림개수").data(notificationCounts.get(postUserId)));

            } catch (IOException e) {

                // IOException 발생 시 SSEEmitter 제거
                NotificationController.sseEmitters.remove(postUserId);

            }
        }
    }

    // 로컬 좋아요 알림
    @Transactional
    public void notifyLocalLike (Long postId, Long userId) {

        // postId로부터 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 해당 게시물에 대한 좋아요 조회
        Like receiveLike = (Like) likeRepository.findFirstByPostIdAndUserIdAndLikeCategoryEnum(postId, userId, LOCAL_LIKE)
                .orElseThrow(()-> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        // 해당 게시물의 작성자 ID 조회 (알림 받을 사람)
        Long postUserId = post.getUser().getId();

        // 해당 작성자에게 SSEEmitter가 존재하는지 확인
        if (NotificationController.sseEmitters.containsKey(postUserId)) {

            // SSEEmitter가 존재한다면 해당 SSEEmitter를 가져옴
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(postUserId);

            try {
                // SSE 이벤트에 담을 데이터 구성
                Map <String, String> eventData = new HashMap<>();

                eventData.put("message", receiveLike.getUser().getNickname() + "님이 주민 추천 좋아요를 눌렸습니다.");
                eventData.put("createdAt", String.valueOf(LocalDateTime.now()));
//                eventData.put("sender", receiveLike.getUser().getNickname());

                // SSE 이벤트를 통해 데이터 전송
                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                // Notification 객체 생성 및 저장

                Notification notification = Notification.builder()
                        .sender(receiveLike.getUser().getNickname())
                        .createdAt(LocalDateTime.now())
                        .user(post.getUser())
                        .like(receiveLike)
                        .build();

                notificationRepository.save(notification);

                // 알림 개수 증가
                notificationCounts.put(postUserId, notificationCounts.getOrDefault(postUserId,0) + 1);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount알림개수").data(notificationCounts.get(postUserId)));

            } catch (IOException e) {

                // IOException 발생 시 SSEEmitter 제거
                NotificationController.sseEmitters.remove(postUserId);

            }
        }
    }

    // 알림 삭제
    public NotiDeleteResponseDto deleteNotification (Long id) throws IOException {

        Notification notification = notificationRepository.findById(id)
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

}
