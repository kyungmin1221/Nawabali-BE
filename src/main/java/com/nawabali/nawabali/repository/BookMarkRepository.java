package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.BookMark;
import com.nawabali.nawabali.repository.querydsl.bookmark.BookmarkDslRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long> , BookmarkDslRepositoryCustom {

    Optional<BookMark> findByUserIdAndPostId(Long userId, Long postId);

    List<BookMark> findByUserId(Long userId);
}
