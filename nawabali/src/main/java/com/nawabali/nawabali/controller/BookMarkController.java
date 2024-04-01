package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.BookMarkDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarks/posts")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @PostMapping
    public ResponseEntity<BookMarkDto.ResponseDto> createBookMark(@RequestParam Long userId,
                                                                      @RequestParam Long postId) {
        BookMarkDto.ResponseDto responseDto = bookMarkService.createBookMark(userId, postId);
        return ResponseEntity.ok(responseDto);
    }

    // 북마크 삭제
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookMark(@PathVariable Long bookmarkId) {
        bookMarkService.deleteBookMark(bookmarkId);
        return ResponseEntity.ok().build();
    }
}
