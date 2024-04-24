package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.NotiDeleteResponseDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "알림 관련 API", description = "알림 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Operation(summary = "SSE 알림 연결" , description = "연결, 구독 되어 있어야만 알림이 전송됨")
    @GetMapping ("/notification/subscribe")
    public SseEmitter subscribe (@AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();

        SseEmitter sseEmitter = notificationService.subscribe(userId);

        return sseEmitter;
    }

    @Operation(summary = "알림 삭제" , description = "해당 알림 삭제 API")
    @DeleteMapping ("/notification/delete/{notificationId}")
    public NotiDeleteResponseDto deleteNotification (@PathVariable Long notificationId) throws IOException {
        return notificationService.deleteNotification(notificationId);
    }

}
