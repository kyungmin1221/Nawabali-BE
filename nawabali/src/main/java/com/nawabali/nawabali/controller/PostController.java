package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto.ResponseDto> createPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody PostDto.RequestDto requestDto) {
        log.info("userDetails.username : " + userDetails.getUsername());
        log.info("userDetails.password : " + userDetails.getPassword());
        log.info("userDetails.getuser : " + userDetails.getUser());

        PostDto.ResponseDto responseDto = postService.createPost(userDetails.getUser(), requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
