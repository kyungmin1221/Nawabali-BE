package com.nawabali.nawabali.repository.dslrepository;

import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostDslRepositoryCustomImpl implements PostDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public PostDslRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Slice<Post> findPostsByLatest(Pageable pageable) {
        QPost post = QPost.post;
        List<Post> posts = queryFactory
                .selectFrom(post)
                .orderBy(post.createdAt.desc())     // 최신순으로 정렬(맨위가 최신)
                .offset(pageable.getOffset())
                // Slice는 카운트 쿼리가 없는 대신에 게시물이 더 존재 하는지 알아야 함.
                // 따라서 요청한 size 보다 1개 더 넣어줘서 반환된 개수를 통해 다음 페이지 여부를 판단
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if (posts.size() > pageable.getPageSize()) {    // 다음 페이지가 존재하는지 확인(다음 페이지의 존재 유무를 확인하기 위함)
            posts.remove(posts.size() - 1);
            hasNext = true;     // 다음 페이지가 있음을 알려줌 false -> true
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

}
