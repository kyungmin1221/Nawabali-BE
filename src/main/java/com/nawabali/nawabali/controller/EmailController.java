package com.nawabali.nawabali.controller;


import com.nawabali.nawabali.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "이메일 인증 API", description = "이메일 인증 관련 API 입니다.")
@RestController
@RequestMapping("/email-verification")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "구글 이메일 인증 코드 전송",
            description = "이메일을 입력하여 이메일 코드를 받습니다.",
            parameters = {
                    @Parameter(name = "email", description = "인증번호를 받을 이메일 주소", example = "aaa@gmail.com")
            }
    )
    @PostMapping
    public ResponseEntity<?> sendCode( @RequestParam("email") String email) {
        emailService.sendCodeToEmail(email);
        return ResponseEntity.ok().body("발송완료");
    }


    @Operation(summary = "구글 이메일 인증",
            description = "받은 코드로 이메일 인증을 진행합니다.",
            parameters = {
                    @Parameter(name = "email", description = "인증번호를 받은 이메일 주소", example = "aaa@gmail.com"),
                    @Parameter(name = "code", description = "받은 인증번호", example = "123456")
            })
    @GetMapping
    public ResponseEntity<?> verifyCode(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        return ResponseEntity.ok().body(emailService.verifyCode(email, code));
    }
}

