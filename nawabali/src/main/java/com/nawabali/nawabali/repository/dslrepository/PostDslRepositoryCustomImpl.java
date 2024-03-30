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
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if (posts.size() > pageable.getPageSize()) {
            posts.remove(posts.size() - 1);
            hasNext = true;
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

}
