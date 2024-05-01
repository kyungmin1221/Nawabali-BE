package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.dto.ChatDto.ChatMessageResponseDto;
import com.nawabali.nawabali.dto.ChatDto.ChatRoomDto;
import com.nawabali.nawabali.dto.ChatDto.ChatRoomListResponseDto;
import com.nawabali.nawabali.dto.ChatDto.ChatRoomSearchListDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Tag(name = "채팅방 관련 API", description = "채팅방 관련 API 입니다.")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 생성" , description = "상대방 nickname으로  채팅방 생성 API")
    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto createRoom (@RequestParam("type") ChatRoomEnum chatRoomEnum,
                                   @RequestParam String roomName,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createRoom(chatRoomEnum, roomName, userDetails.getUser());
    }

    @Operation(summary = "본인 채팅방 전체 목록" , description = "본인 채팅방 전체 목록 조회 API")
    @GetMapping("/rooms")
    @ResponseBody
    public Slice<ChatRoomListResponseDto> room (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @PageableDefault(size = 10) Pageable pageable) {
        return chatRoomService.room(userDetails.getUser(), pageable);
    }

    @Operation(summary = "채팅방 / 메세지 내용 검색" , description = "채팅방 / 메세지 검색 API")
    @GetMapping("/room/found")
    @ResponseBody
    public Slice <ChatRoomSearchListDto> roomInfo (@RequestParam String roomName,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PageableDefault(size = 10) Pageable pageable ) {
        return chatRoomService.roomInfo(roomName, userDetails.getUser(), pageable);
    }

    @Operation(summary = "채팅방 전체 대화 내용 조회" , description = "채팅방 전제 대화 내용 조회 API")
    @GetMapping("/room/{roomId}/message")
    public ResponseEntity<Slice<ChatMessageResponseDto>> loadMessage (@PathVariable Long roomId,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                     @PageableDefault(
                                                                             size = 15,
                                                                             sort = "createdMessageAt",
                                                                             direction = Sort.Direction.DESC)
                                                                         Pageable pageable) {
        Slice<ChatMessageResponseDto> messages = chatRoomService.loadMessage(roomId, userDetails.getUser(), pageable);
        return ResponseEntity.ok(messages);
    }
}