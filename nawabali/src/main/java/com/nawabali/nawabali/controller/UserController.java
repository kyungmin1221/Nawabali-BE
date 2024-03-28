package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public SignupDto.SignupResponseDto signup(@RequestBody SignupDto.SignupRequestDto requestDto){
        return userService.signup(requestDto);
    }

}
