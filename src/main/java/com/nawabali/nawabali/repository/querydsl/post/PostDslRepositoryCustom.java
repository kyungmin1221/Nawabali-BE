package com.nawabali.nawabali.repository.querydsl.post;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Period;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.querydsl.PostDslDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostDslRepositoryCustom {

    Slice<PostDslDto.ResponseDto> findPostsByLatest(Pageable pageable);

    Slice<PostDslDto.ResponseDto> findCategoryByPost(Category category, String district, Pageable pageable);
    List<PostDslDto.SearchDto> findSearchByPosts(String contents);

    List<PostDslDto.ResponseDto> topLikeByPosts(Category category, String district, Period period);

    Slice<PostDto.ResponseDto> getMyPosts(Long userId, Pageable pageable, Category category);

}
