package com.nawabali.nawabali.repository.dslrepository;

import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.domain.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDslRepositoryCustomImpl implements CommentDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public CommentDslRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Slice<Comment> findCommentsByPostId(Long postId, Pageable pageable) {
        QComment comment = QComment.comment;

        List<Comment> comments = queryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.parent)
                .where(comment.post.id.eq(postId))
                .orderBy(
                        comment.parent.Id.asc().nullsFirst(),
                        comment.createdAt.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = comments.size() > pageable.getPageSize();
        if (hasNext) {
            comments.remove(comments.size() - 1);
        }

        return new SliceImpl<>(comments, pageable, hasNext);
    }
}
