package com.nawabali.nawabali;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/ping")
    public String check() {
        return "Pong! substring 제거";
    }
}
