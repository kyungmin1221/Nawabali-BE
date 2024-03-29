package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository <Comment, Long> {
}