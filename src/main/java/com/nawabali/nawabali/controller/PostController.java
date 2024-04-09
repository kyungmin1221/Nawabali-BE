package com.nawabali.nawabali.controller;


import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.querydsl.PostDslDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "게시물 API", description = "게시물 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시물 생성" , description =
            """
            - form-data 형식의 게시물 생성
            - key :  requestDto , value : {"title":"title", "contents":"contents", "category":"category", "latitude": latitude, "longitude": longitude}'
            - key : files , value : 이미지 파일 최대 5장
            """)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostDto.ResponseDto> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart("requestDto") PostDto.RequestDto requestDto,
            @RequestPart("files") List<MultipartFile> files) {
        PostDto.ResponseDto responseDto = postService.createPost(userDetails.getUser(), requestDto, files);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "게시물 전제 조회", description =
            """
            - 게시물 조회시 무한 스크롤 구현 , 10장씩 보이고 그 이상 페이지가 넘어갈 경우 데이터 확인 후 페이지 이동
            """)
    @GetMapping
    public ResponseEntity<Slice<PostDto.ResponseDto>> getPostsByLatest(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<PostDto.ResponseDto> postsSlice = postService.getPostsByLatest(pageable);
        return ResponseEntity.ok(postsSlice);
    }

    @Operation(summary = "게시물 카테고리 or 구 로 조회", description = "category 또는 district 를 이용한 게시물 조회")
    @GetMapping("/filtered")
    public ResponseEntity<Slice<PostDto.ResponseDto>> getPostByFiltered(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String district,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        Slice<PostDto.ResponseDto> posts = postService.getPostByCategory(category, district, pageable);
        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "게시물 상세 조회", description = "postId 를 이용한 게시물 상세 조회, 댓글 조회 api는 따로 구현")
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.ResponseDetailDto> getPost(@PathVariable Long postId) {
        PostDto.ResponseDetailDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok(responseDto);
    }



    @Operation(summary = "게시물 수정", description = "postId 를 이용한 게시물 수정")
    @PatchMapping("/{postId}")
    public ResponseEntity<PostDto.PatchDto> updatePost(@PathVariable Long postId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody PostDto.PatchDto patchDto) {
        PostDto.PatchDto responseDto = postService.updatePost(postId,userDetails.getUser(),patchDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "게시물 삭제", description = "postId 를 이용한 게시물 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDto.DeleteDto> deletePost(@PathVariable Long postId) {
        PostDto.DeleteDto deleteDto = postService.deletePost(postId);
        return ResponseEntity.ok(deleteDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDslDto.SearchDto>> searchPost(@RequestParam("query") String contents) {
        List<PostDslDto.SearchDto> postDslDto = postService.searchPost(contents);
        return ResponseEntity.ok(postDslDto);
    }
}

