package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.BookMark;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.BookMarkDto;
import com.nawabali.nawabali.repository.BookMarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final PostService postService;

    @Transactional
    public BookMarkDto.ResponseDto toggleBookmark(User user, Long postId) {
        Post post = postService.getPostId(postId);

        Optional<BookMark> bookmark = bookMarkRepository.findByUserIdAndPostId(user.getId(), postId);

        if (bookmark.isPresent()) {
            return removeBookmark(bookmark.get());
        } else {
            return addBookmark(user, post);
        }
    }


    // 유저의 북마크 조회
    public Slice<BookMarkDto.UserBookmarkDto> getBookmarks(User user, Pageable pageable) {
       return bookMarkRepository.getUserBookmarks(user , pageable);
    }


    public BookMarkDto.ResponseDto removeBookmark(BookMark bookmark) {
        bookMarkRepository.delete(bookmark);
        return new BookMarkDto.ResponseDto(false,
                bookmark.getId(),
                bookmark.getPost().getId(),
                bookmark.getUser().getId(),
                bookmark.getCreatedAt());
    }

    public BookMarkDto.ResponseDto addBookmark(User user, Post post) {
        BookMark bookmark = BookMark.builder()
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        bookMarkRepository.save(bookmark);

        return new BookMarkDto.ResponseDto(true,
                bookmark.getId(),
                bookmark.getPost().getId(),
                bookmark.getUser().getId(),
                bookmark.getCreatedAt());
    }


}

