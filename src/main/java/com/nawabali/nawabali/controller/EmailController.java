package com.nawabali.nawabali.controller;


import com.nawabali.nawabali.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/email-verification")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;


    @PostMapping
    public ResponseEntity<?> sendCode(
            @RequestParam("email")
            String email) {
        emailService.sendCodeToEmail(email);
        return ResponseEntity.ok().body("발송완료");
    }


    @GetMapping
    public ResponseEntity<?> verifyCode(
            @RequestParam("email")
            String email,
            @RequestParam("code")
            String code) {
        return ResponseEntity.ok().body(emailService.verifyCode(email, code));
    }
}

