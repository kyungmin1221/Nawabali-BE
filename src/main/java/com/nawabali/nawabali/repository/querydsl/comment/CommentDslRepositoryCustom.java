package com.nawabali.nawabali.repository.querydsl.comment;

import com.nawabali.nawabali.dto.CommentDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentDslRepositoryCustom {
    //findCommentByPostIdWithParentOrderByParentIdAscNullsFirstCreatedAtAsc
    Slice<CommentDto.GetResponseDto> findCommentsByPostId(Long postId, Pageable pageable);
}