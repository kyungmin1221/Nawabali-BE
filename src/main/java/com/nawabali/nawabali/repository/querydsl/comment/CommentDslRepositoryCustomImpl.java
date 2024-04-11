package com.nawabali.nawabali.repository.querydsl.comment;

import com.nawabali.nawabali.domain.QComment;
import com.nawabali.nawabali.dto.querydsl.CommentDslDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentDslRepositoryCustomImpl implements CommentDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<CommentDslDto.ResponseDto> findCommentsByPostId(Long postId, Pageable pageable) {
        QComment comment = QComment.comment;

        List<CommentDslDto.ResponseDto> comments = queryFactory
                .select(Projections.bean(CommentDslDto.ResponseDto.class,
                        comment.contents,
                        comment.user.nickname))
                .from(comment)
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = comments.size() > pageable.getPageSize();
        if(hasNext) {
            comments.remove(comments.size() - 1);
        }

        return new SliceImpl<>(comments, pageable, hasNext);

    }
}
