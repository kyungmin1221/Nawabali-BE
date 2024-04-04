package com.nawabali.nawabali.repository.dslrepository;

import com.nawabali.nawabali.dto.dslDto.CommentDslDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentDslRepositoryCustom {
    Slice<CommentDslDto.ResponseDto> findCommentsByPostId(Long postId, Pageable pageable);
}
