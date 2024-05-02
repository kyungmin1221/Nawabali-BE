package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@Tag(name = "알림 관련 API", description = "알림 관련 API 입니다.")
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Operation(summary = "SSE 알림 연결" , description = "subscribe 되어 있어야만 알림이 전송됨")
    @GetMapping(value = "/notification/subscribe", produces = "text/event-stream")
    public CompletableFuture<SseEmitter> subscribe (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.subscribe(userDetails.getUser());
    }
}
