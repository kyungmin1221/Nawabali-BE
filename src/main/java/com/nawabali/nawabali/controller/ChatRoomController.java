package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.ChatDto;
import com.nawabali.nawabali.repository.ChatRoomRepository;
import com.nawabali.nawabali.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;

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
}