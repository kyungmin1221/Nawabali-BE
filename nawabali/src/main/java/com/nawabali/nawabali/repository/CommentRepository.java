package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.dto.CommentDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository <Comment, Long> {
    Optional<Comment> findByPostIdAndId(Long postId, Long commentId);

    List<CommentDto.ResponseDto> findByPostId(Long postId);
}