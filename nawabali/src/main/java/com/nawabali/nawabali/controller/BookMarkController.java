package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.BookMarkDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.BookMarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "북마크 API", description = "북마물 관련 API 입니다.")
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @Operation(summary = "북마크 생성", description = "postId 를 이용한 북마크 생성, 토글 형태로 생성 / 삭제 구현")
    @PostMapping("/posts/{postId}")
    public ResponseEntity<BookMarkDto.ResponseDto> createBookMark(@PathVariable Long postId,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookMarkDto.ResponseDto responseDto = bookMarkService.toggleBookmark(userDetails.getUser(), postId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "회원 북마크 조회", description = "로그인 되어있는 사용자의 북마크를 조회")
    @GetMapping("/users")
    public ResponseEntity<List<BookMarkDto.UserBookmarkDto>> getBookmarks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BookMarkDto.UserBookmarkDto> userBookmarkDto = bookMarkService.getBookmarks(userDetails.getUser());
        return ResponseEntity.ok(userBookmarkDto);
    }

}
