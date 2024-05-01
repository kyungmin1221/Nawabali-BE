package com.nawabali.nawabali.repository.querydsl.comment;

import com.nawabali.nawabali.dto.querydsl.CommentDslDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentDslRepositoryCustom {
    //findCommentByPostIdWithParentOrderByParentIdAscNullsFirstCreatedAtAsc
    Slice<CommentDslDto.ResponseDto> findCommentsByPostId(Long postId, Pageable pageable);
}
