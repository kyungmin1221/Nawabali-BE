package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.dto.CommentDto;
import com.nawabali.nawabali.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
@Slf4j(topic = "CommentController 로그")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/posts/{postId}")
    public CommentDto.ResponseDto createComment (@PathVariable("postId") Long postId,
                                                 @RequestBody CommentDto.RequestDto dto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.createComment(postId, dto, userDetails.getUsername());
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public CommentDto.ResponseDto updateComment (@PathVariable("commentId") Long commentId,
                                                 @RequestBody CommentDto.RequestDto dto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.updateComment(commentId, dto, userDetails.getUsername());
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public CommentDto.ResponseDto deleteComment (@PathVariable("commentId") Long commentId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.deleteComment (commentId, userDetails.getUsername());
    }
}