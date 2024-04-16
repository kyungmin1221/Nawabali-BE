package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import com.nawabali.nawabali.domain.BookMark;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.BookMarkDto;
import com.nawabali.nawabali.dto.querydsl.BookmarkDslDto;
import com.nawabali.nawabali.repository.BookMarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    public Slice<BookmarkDslDto.UserBookmarkDto> getBookmarks(User user, Pageable pageable) {
        Slice<BookmarkDslDto.UserBookmarkDto> bookmarkSlice = bookMarkRepository.getUserBookmarks(user, pageable);
        List<BookmarkDslDto.UserBookmarkDto> content = bookmarkSlice.getContent().stream()
                .map(this::createBookmarkDto)
                .collect(Collectors.toList());

        return new SliceImpl<>(content,pageable,bookmarkSlice.hasNext());
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

    public BookMarkDto.ResponseDto removeBookmark(BookMark bookmark) {
        bookMarkRepository.delete(bookmark);
        return new BookMarkDto.ResponseDto(false,
                bookmark.getId(),
                bookmark.getPost().getId(),
                bookmark.getUser().getId(),
                bookmark.getCreatedAt());
    }

    public BookmarkDslDto.UserBookmarkDto createBookmarkDto(BookmarkDslDto.UserBookmarkDto bookmark) {
        Long likesCount = postService.getLikesCount(bookmark.getPostId(), LikeCategoryEnum.LIKE);
        Long localLikesCount = postService.getLikesCount(bookmark.getPostId(), LikeCategoryEnum.LOCAL_LIKE);

        return new BookmarkDslDto.UserBookmarkDto(
                bookmark.getUserId(),
                bookmark.getPostId(),
                bookmark.getNickname(),
                bookmark.getContents(),
                bookmark.getCategory(),
                bookmark.getDistrict(),
                bookmark.getLatitude(),
                bookmark.getLongitude(),
                bookmark.getCreatedAt(),
                bookmark.getImageUrls(),
                likesCount,
                localLikesCount,
                bookmark.getCommentCount()
        );
    }

}

