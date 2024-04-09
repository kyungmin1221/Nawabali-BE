package com.nawabali.nawabali.repository.querydsl.post;

import com.nawabali.nawabali.dto.querydsl.PostDslDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostDslRepositoryCustom {

    Slice<PostDslDto.ResponseDto> findPostsByLatest(Pageable pageable);

    Slice<PostDslDto.ResponseDto> findCategoryByPost(String category, String district, Pageable pageable);
    List<PostDslDto.SearchDto> findSearchByPosts(String contents);

}
