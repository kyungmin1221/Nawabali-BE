package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.LikeDto;
import com.nawabali.nawabali.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
@Slf4j (topic = "LikeController 로그")
public class LikeController {

    public final LikeService likeService;

    // 좋아요 추가 및 취소
    @PatchMapping("/{postId}/likes")
    public LikeDto.responseDto toggleLike(@PathVariable("postId") Long postId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return likeService.toggleLike(postId, userDetails.getUsername());
    }

    @PatchMapping("/{postId}/localLikes")
    public LikeDto.responseDto toggleLocalLike(@PathVariable("postId") Long postId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return likeService.toggleLocalLike(postId, userDetails.getUsername());
    }

    // 좋아요 취소
//    @DeleteMapping("/{postId}/likes")
//    public LikeDto.responseDto deleteLike (@PathVariable("postId") Long postId,
//                                           @RequestBody LikeDto.requestDto dto,
//                                           @AuthenticationPrincipal UserDetails userDetails) {
//        return likeService.deleteLike (postId, dto, userDetails.getUsername());
//    }

//    // localLike 좋아요 추가
//    @PostMapping("/{postId}/locallikes")
//    public LikeDto.responseDto createLocalLike (@PathVariable("postId") Long postId,
//                                                @RequestBody LikeDto.requestDto dto,
//                                                @AuthenticationPrincipal UserDetails userDetails) {
//        return likeService.createLocalLike (postId, dto, userDetails.getUsername());
//    }
//
//    // localLike 좋아요 취소
//    @DeleteMapping("/{postId}/locallikes")
//    public LikeDto.responseDto deleteLocalLike (@PathVariable("postId") Long postId,
//                                                @RequestBody LikeDto.requestDto dto,
//                                                @AuthenticationPrincipal UserDetails userDetails) {
//        return likeService.deleteLocalLike (postId, dto, userDetails.getUsername());
//    }
}