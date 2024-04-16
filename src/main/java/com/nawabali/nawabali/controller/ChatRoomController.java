package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.constant.ChatRoomEnum;
import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.ChatMessageService;
import com.nawabali.nawabali.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅방 관련 API", description = "채팅방 관련 API 입니다.")
@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅 메인 화면" , description = "html 파일 구현으로 임시로 넣어놓음")
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    @Operation(summary = "채팅방 입장 화면" , description = "html 파일 구현으로 임시로 넣어놓음")
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    @Operation(summary = "채팅방 생성" , description = "채팅방 name으로 채팅방 생성 API")
    @PostMapping("/room")
    @ResponseBody
    public ChatDto.ChatRoomDto createRoom(@RequestParam("type") ChatRoomEnum chatRoomEnum,
                                          @RequestParam String roomName,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createRoom(chatRoomEnum, roomName, userDetails.getUser());
    }

    @Operation(summary = "본인 전체 채팅방 목록 반환" , description = "본인 전체 채팅방 조회 API")
    @GetMapping("/rooms")
    @ResponseBody
    public Slice<ChatDto.ChatRoomListDto> room(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PageableDefault(
                                                  size = 10,
                                                  sort = "Id",
                                                  direction = Sort.Direction.DESC)Pageable pageable) {
        Slice<ChatDto.ChatRoomListDto> chatRoomDtoSlice = chatRoomService.room(userDetails.getUser().getId(), pageable);
        return chatRoomDtoSlice;
    }

    @Operation(summary = "특정 채팅방 조회" , description = "채팅방 검색 API")
    @GetMapping("/room/found")
    @ResponseBody
    public List<ChatDto.ChatRoomDto> roomInfo(@RequestParam String roomName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.roomInfo(roomName, userDetails.getUser());
    }

    @Operation(summary = "채팅방 대화 내용 조회" , description = "채팅방 전제 대화 내용 조회 API")
    @GetMapping("/room/{roomId}/message")
    public List<ChatDto.ChatMessageDto> loadMessage (@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.loadMessage(roomId, userDetails.getUser());
    }  // ChatController는 웹소켓 endpoint를 담당해서 일반적인 http요청을 처리하지 않아 이곳으로 옮겨 놓음.

}