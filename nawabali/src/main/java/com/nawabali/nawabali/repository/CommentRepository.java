package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository <Comment, Long> {
}
