package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.PostService;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto.ResponseDto> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestPart("requestDto") PostDto.RequestDto requestDto,
            @RequestParam("files") List<MultipartFile> files) {
        PostDto.ResponseDto responseDto = postService.createPost(userDetails.getUser(), requestDto, files);
        return ResponseEntity.ok(responseDto);
    }


    @GetMapping
    public ResponseEntity<Slice<PostDto.ResponseDto>> getPostsByLatest(
            @PageableDefault(size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Slice<PostDto.ResponseDto> postsSlice = postService.getPostsByLatest(pageable);
        // 조회 결과 반환
        return ResponseEntity.ok(postsSlice);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.ResponseDetailDto> getPost(@PathVariable Long postId) {
        PostDto.ResponseDetailDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostDto.ResponseDto> updatePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody PostDto.PatchDto patchDto) {
        PostDto.ResponseDto responseDto = postService.updatePost(postId,userDetails.getUser(),patchDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDto.DeleteDto> deletePost(@PathVariable Long postId) {
        PostDto.DeleteDto deleteDto = postService.deletePost(postId);
        return ResponseEntity.ok(deleteDto);
    }
}

