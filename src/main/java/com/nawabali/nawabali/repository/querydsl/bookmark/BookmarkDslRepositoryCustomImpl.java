package com.nawabali.nawabali.repository.querydsl.bookmark;

import com.nawabali.nawabali.domain.QBookMark;
import com.nawabali.nawabali.domain.QPost;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.BookMarkDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookmarkDslRepositoryCustomImpl implements BookmarkDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public Slice<BookMarkDto.UserBookmarkDto> getUserBookmarks(User user, Pageable pageable) {
        QBookMark bookMark = QBookMark.bookMark;
        QPost post = QPost.post;

        List<BookMarkDto.UserBookmarkDto> bookmarks = queryFactory
                .select(Projections.bean(BookMarkDto.UserBookmarkDto.class,
                        bookMark.id.as("bookmarkId"),
                        bookMark.post.id.as("postId"),
                        bookMark.user.id.as("userId"),
                        bookMark.createdAt))
                .from(bookMark)
                .where(bookMark.user.id.eq(user.getId()))
                .join(bookMark.post, post)
                .orderBy(bookMark.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = bookmarks.size() > pageable.getPageSize();
        if(hasNext) {
            bookmarks.remove(bookmarks.size() - 1);
        }

        return new SliceImpl<>(bookmarks, pageable, hasNext);
    }

}
