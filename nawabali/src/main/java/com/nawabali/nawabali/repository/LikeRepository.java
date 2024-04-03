package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import com.nawabali.nawabali.domain.Like;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository <Like, Long> {

    Optional<Like> findByUserAndPost (User user, Post post);
    Like findByUserIdAndPostId(Long userId, Long postId) ;
    Long countByPostIdAndLikeCategoryEnum(Long postId, LikeCategoryEnum likeCategory);

    Like findByUserIdAndPostIdAndLikeCategoryEnum(Long id, Long postId, LikeCategoryEnum likeCategoryEnum);
}