package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.dto.CommentDto;
import com.nawabali.nawabali.repository.querydsl.comment.CommentDslRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository <Comment, Long> , CommentDslRepositoryCustom {
    Optional<Comment> findByPostIdAndId(Long postId, Long commentId);

    List<CommentDto.ResponseDto> findByPostId(Long postId);
}