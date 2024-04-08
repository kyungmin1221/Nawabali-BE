package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.NotiDeleteResponseDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    // 메세지 알림 | SSE 연결, 구독
    @GetMapping ("/notification/subscribe")
    public SseEmitter subscribe (@AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();

        SseEmitter sseEmitter = notificationService.subscribe(userId);

        return sseEmitter;
    }

    // 알림 삭제
    @DeleteMapping ("/notification/delete/{id}") //***** Dto 만들기? 여기서 id 뭔지 확인하고 바꾸기)
    public NotiDeleteResponseDto deleteNotification (@PathVariable Long id) throws IOException {
        return notificationService.deleteNotification(id);
    }
}
