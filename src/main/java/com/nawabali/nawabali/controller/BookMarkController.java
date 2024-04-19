package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.BookMarkDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.BookMarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "북마크 API", description = "북마크 관련 API 입니다.")
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @Operation(summary = "북마크 생성",
            description = "postId 를 이용한 북마크 생성," +
                    "토글 형태로 북마크를 선택하지 않았었다면 북마크를 추가, 이미 추가되어 있었다면 북마크를 취소함")
    @PostMapping("/posts/{postId}")
    public ResponseEntity<BookMarkDto.ResponseDto> createBookMark(@PathVariable Long postId,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookMarkDto.ResponseDto responseDto = bookMarkService.toggleBookmark(userDetails.getUser(), postId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "회원의 북마크 조회(무한 스크롤)",
            description = "로그인 되어있는 사용자의 북마크를 조회",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", description = "페이지 당 게시물 수", example = "10"),
                    @Parameter(name = "sort", description = "정렬 기준과 방향, 예: createdAt,desc(생성일 내림차순 정렬)", example = "createdAt,desc")
            })
    @GetMapping("/users")
    public ResponseEntity<Slice<BookMarkDto.UserBookmarkDto>> getBookmarks(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @PageableDefault(
                                                                                   size = 10,
                                                                                   sort = "createdAt",
                                                                                   direction = Sort.Direction.DESC) Pageable pageable ){
        Slice<BookMarkDto.UserBookmarkDto> userBookmarkDto = bookMarkService.getBookmarks(userDetails.getUser(), pageable);
        return ResponseEntity.ok(userBookmarkDto);
    }

}
