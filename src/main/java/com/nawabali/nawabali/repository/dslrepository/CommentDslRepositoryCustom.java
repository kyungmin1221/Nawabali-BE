package com.nawabali.nawabali.repository.dslrepository;

import com.nawabali.nawabali.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentDslRepositoryCustom {
    //findCommentByPostIdWithParentOrderByParentIdAscNullsFirstCreatedAtAsc
    Slice<Comment> findCommentsByPostId(Long postId, Pageable pageable);
}
