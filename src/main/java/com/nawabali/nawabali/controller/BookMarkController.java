package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.dto.BookMarkDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<BookMarkDto.ResponseDto> createBookMark(@PathVariable Long postId,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookMarkDto.ResponseDto responseDto = bookMarkService.toggleBookmark(userDetails.getUser(), postId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/users")
    public ResponseEntity<List<BookMarkDto.UserBookmarkDto>> getBookmarks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BookMarkDto.UserBookmarkDto> userBookmarkDto = bookMarkService.getBookmarks(userDetails.getUser());
        return ResponseEntity.ok(userBookmarkDto);
    }

}
