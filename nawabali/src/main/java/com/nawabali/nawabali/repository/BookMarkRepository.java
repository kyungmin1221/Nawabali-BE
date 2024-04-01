package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.BookMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    Optional<BookMark> findByUserIdAndPostId(Long userId, Long postId);

}
