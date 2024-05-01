package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import com.nawabali.nawabali.domain.Like;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository <Like, Long> {

//    Optional<Like> findByUserAndPost (User user, Post post);
    Long countByPostIdAndLikeCategoryEnum(Long postId, LikeCategoryEnum likeCategory);
    Long countByPostIdInAndLikeCategoryEnum(List<Long> postIds, LikeCategoryEnum likeCategoryEnum);
    Like findByUserIdAndPostIdAndLikeCategoryEnum(Long userId, Long postId, LikeCategoryEnum likeCategoryEnum);
    Optional<Object> findFirstByPostIdAndUserIdAndLikeCategoryEnum(Long postId, Long userId, LikeCategoryEnum likeCategoryEnum);

    Optional<Long> countByPostTownDistrictAndLikeCategoryEnum(String district, LikeCategoryEnum likeCategoryEnum);
}