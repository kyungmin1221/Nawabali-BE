package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.repository.ChatMessageRepository;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.service.ChatMessageService;
import com.nawabali.nawabali.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅 관련 API", description = "채팅 관련 API 입니다.")
@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatDto.ChatRoomDto> room() {

        return chatRoomService.room();

//        return chatRoomRepository.findAllRoom();

    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatDto.ChatRoomDto createRoom(@RequestParam String name) {

        return chatRoomService.createRoom(name);

//        return chatRoomRepository.createChatRoom(name);
    }

    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    // 특정 채팅방 조회
    @GetMapping("/room/found")
    @ResponseBody
    public List<ChatDto.ChatRoomDto> roomInfo(@RequestParam String name) {
        return chatRoomService.roomInfo(name);
//        return chatRoomRepository.findRoomById(roomId);
    }

        // ChatController는 웹소켓 endpoint를 담당해서 일반적인 http요청을 처리하지 않아 이곳으로 옮겨 놓음.
//    @GetMapping("/room/{roomId}/message")
//    public List<ChatDto.ChatMessageDto> loadMessage (@PathVariable Long roomId) {
//        return chatMessageService.loadMessage(roomId);
//    }
}