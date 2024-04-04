package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.CommentDto;
import com.nawabali.nawabali.dto.dslDto.CommentDslDto;
import com.nawabali.nawabali.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API", description = "댓 관련 API 입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("/comments")
@Slf4j(topic = "CommentController 로그")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @Operation(summary = "게시물 댓글 작성", description = "postId 를 이용한 게시물에 댓글 생성")
    @PostMapping("/posts/{postId}")
    public CommentDto.ResponseDto createComment (@PathVariable("postId") Long postId,
                                                 @RequestBody CommentDto.RequestDto dto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.createComment(postId, dto, userDetails.getUsername());
    }

    // 댓글 조회(무한 스크롤)
    @Operation(summary = "게시물 댓글 조회", description = "postId 를 이용한 게시물 댓글 조회")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Slice<CommentDslDto.ResponseDto>> getComments(@PathVariable Long postId,
                                                                        @PageableDefault(size = 10) Pageable pageable) {
        Slice<CommentDslDto.ResponseDto> comments = commentService.getComments(postId,pageable);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @Operation(summary = "게시물 댓글 수정", description = "commentId 를 이용한 게시물에 댓글 수정")
    @PatchMapping("/{commentId}")
    public CommentDto.ResponseDto updateComment (@PathVariable("commentId") Long commentId,
                                                 @RequestBody CommentDto.RequestDto dto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.updateComment(commentId, dto, userDetails.getUsername());
    }

    // 댓글 삭제
    @Operation(summary = "게시물 댓글 삭제", description = "commentId 를 이용한 게시물에 댓글 삭제")
    @DeleteMapping("/{commentId}")
    public CommentDto.DeleteResponseDto deleteComment (@PathVariable("commentId") Long commentId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.deleteComment (commentId, userDetails.getUsername());
    }

}