package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.LikeDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "좋아요 API", description = "좋아요 관련 API 입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class LikeController {

    public final LikeService likeService;

    @Operation(summary = "게시물 좋아요 생성 및 삭제", description = "postId 를 이용하여 토클 형태의 좋아요 클릭")
    @PatchMapping("/{postId}/likes")
    public LikeDto.responseDto toggleLike(@PathVariable("postId") Long postId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.toggleLike(postId, userDetails.getUsername());
    }

    @Operation(summary = "게시물 지역주민 좋아요 생성 및 삭제", description = "postId 를 이용하여 토클 형태의 지역주민 좋아요 클릭")
    @PatchMapping("/{postId}/localLikes")
    public LikeDto.responseDto toggleLocalLike(@PathVariable("postId") Long postId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return likeService.toggleLocalLike(postId, userDetails.getUsername());
    }
}