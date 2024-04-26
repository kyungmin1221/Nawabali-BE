package com.nawabali.nawabali.repository.querydsl.post;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Period;
import com.nawabali.nawabali.dto.PostDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostDslRepositoryCustom {

    Slice<PostDto.ResponseDto> findPostsByLatest(Pageable pageable);

    Slice<PostDto.ResponseDto> findCategoryByPost(Category category, String district, Pageable pageable);

    List<PostDto.ResponseDto> topLikeByPosts(Category category, String district, Period period);

    Slice<PostDto.ResponseDto> getMyPosts(Long userId, Pageable pageable, Category category);

    PostDto.SortDistrictDto findDistrictByPost(Category category, Period period);

    List<PostDto.SortCategoryDto> findCategoryByPost(String district);

//    Slice<PostDto.ResponseDto> searchAndFilterPosts(List<Long> postIds, Pageable pageable);

    Slice<PostDto.ResponseDto> getUserPost(Long userId, Category category, Pageable pageable);

}
