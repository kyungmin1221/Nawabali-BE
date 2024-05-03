package com.nawabali.nawabali.repository.querydsl.comment;

import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.domain.QComment;
import com.nawabali.nawabali.dto.CommentDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentDslRepositoryCustomImpl implements CommentDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<CommentDto.GetResponseDto> findCommentsByPostId(Long postId, Pageable pageable) {
        QComment comment = QComment.comment;

        List<Comment> comments = queryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.post.id.eq(postId))
                .orderBy(
                        comment.createdAt.asc(),
                        comment.parent.Id.asc().nullsFirst()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = comments.size() > pageable.getPageSize();
        if (hasNext) {
            comments.remove(comments.size() - 1);
        }

        List<CommentDto.GetResponseDto> responseDtos = comments.stream()
                .map(CommentDto.GetResponseDto::new)
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }
}