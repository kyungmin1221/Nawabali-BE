package com.nawabali.nawabali;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/ping")
    public String check() {
        return "Pong! BEARER_PREFIX 추가";
    }
}
