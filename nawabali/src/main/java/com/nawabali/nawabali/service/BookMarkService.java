package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.BookMark;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.BookMarkDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.BookMarkRepository;
import com.nawabali.nawabali.repository.PostRepository;
import com.nawabali.nawabali.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final PostService postService;
    private final UserService userService;


    @Transactional
    public BookMarkDto.ResponseDto createBookMark(Long postId, Long userId) {
        User user = userService.getUserId(userId);
        Post post = postService.getPostId(postId);

        BookMark bookMark = BookMark.builder()
                .user(user)
                .post(post)
                .build();

        BookMark savedBookMark = bookMarkRepository.save(bookMark);

        return new BookMarkDto.ResponseDto(savedBookMark.getId(), user.getId());
    }

    @Transactional
    public void deleteBookMark(Long bookmarkId) {
        BookMark bookMark = getBookmarkId(bookmarkId);
        bookMarkRepository.delete(bookMark);
    }

    public BookMark getBookmarkId(Long bookmarkId) {
        return bookMarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_BOOKMARK));
    }
}
