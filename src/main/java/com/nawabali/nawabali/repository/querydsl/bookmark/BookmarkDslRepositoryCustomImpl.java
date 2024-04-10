package com.nawabali.nawabali.repository.querydsl.bookmark;

import com.nawabali.nawabali.domain.QBookMark;
import com.nawabali.nawabali.domain.QPost;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.BookMarkDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookmarkDslRepositoryCustomImpl implements BookmarkDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public BookmarkDslRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<BookMarkDto.UserBookmarkDto> getUserBookmarks(User user) {
        QBookMark bookMark = QBookMark.bookMark;
        QPost post = QPost.post;

        List<BookMarkDto.UserBookmarkDto> bookmarks = queryFactory
                .select(Projections.fields(BookMarkDto.UserBookmarkDto.class,
                        bookMark.id.as("bookmarkId"),
                        bookMark.post.id.as("postId"),
                        bookMark.user.id.as("userId") ))
                .from(bookMark)
                .where(bookMark.user.id.eq(user.getId()))
                .join(bookMark.post, post)
                .fetch();

        return bookmarks;
    }


}
