package com.nawabali.nawabali.repository.querydsl.bookmark;

import com.nawabali.nawabali.domain.*;
import com.nawabali.nawabali.domain.image.PostImage;
import com.nawabali.nawabali.dto.BookMarkDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookmarkDslRepositoryCustomImpl implements BookmarkDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public Slice<BookMarkDto.UserBookmarkDto> getUserBookmarks(User user, Pageable pageable) {
        QBookMark bookMark = QBookMark.bookMark;
        QPost post = QPost.post;
        QUser quser = QUser.user;

        List<BookMark> bookmarks = queryFactory
                .selectFrom(bookMark)
                .where(bookMark.user.id.eq(user.getId()))
                .join(bookMark.post, post).fetchJoin()
                .join(post.user ,quser).fetchJoin()
                .orderBy(bookMark.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = bookmarks.size() > pageable.getPageSize();
        if(hasNext) {
            bookmarks.remove(bookmarks.size() - 1);
        }

        List<BookMarkDto.UserBookmarkDto> responseDtos = convertBookmarkDto(bookmarks);
        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }


    private  List<BookMarkDto.UserBookmarkDto> convertBookmarkDto(List<BookMark> bookMarks) {
        return bookMarks.stream()
                .map(bookMark -> BookMarkDto.UserBookmarkDto.builder()
                        .id(bookMark.getId())
                        .postId(bookMark.getPost().getId())
                        .mainImageUrl(bookMark.getPost().getImages().get(1).getImgUrl())
                        .multiImages(bookMark.getPost().getImages().size() > 2)
                        .createdAt(bookMark.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

}
