package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.repository.querydsl.post.PostDslRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , PostDslRepositoryCustom {
    List<PostDto.getMyPostsResponseDto> findByUserId(Long userId);

    Slice<Post> findByUserId(Long userId, Pageable pageable);

    Slice<Post> findByUserIdAndCategory(Long userId, Pageable pageable, Category category);


}
