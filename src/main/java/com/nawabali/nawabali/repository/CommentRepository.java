package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.repository.dslrepository.CommentDslRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository <Comment, Long> , CommentDslRepositoryCustom {

    @Query("select c from Comment c left join fetch c.parent where c.id = :id")
    Optional<Comment> findCommentByIdWithParent(@Param("id") Long id);
}