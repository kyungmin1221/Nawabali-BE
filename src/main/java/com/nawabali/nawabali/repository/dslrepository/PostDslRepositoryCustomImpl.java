package com.nawabali.nawabali.repository.dslrepository;

import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.QPost;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.domain.image.PostImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.dslDto.PostDslDto;
import com.nawabali.nawabali.repository.LikeRepository;
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
import java.util.stream.Collectors;

@Repository
public class PostDslRepositoryCustomImpl implements PostDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public PostDslRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Slice<PostDslDto.ResponseDto> findPostsByLatest(Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();
        if(hasNext) {
            posts.remove(posts.size() - 1);
        }

        List<PostDslDto.ResponseDto> responseDtos = posts.stream()
                .map(newPost -> PostDslDto.ResponseDto.builder()
                        .userId(newPost.getUser().getId())
                        .postId(newPost.getId())
                        .nickname(newPost.getUser().getNickname())
                        .title(newPost.getTitle())
                        .contents(newPost.getContents())
                        .category(newPost.getCategory().name())
                        .createdAt(newPost.getCreatedAt())
                        .modifiedAt(newPost.getModifiedAt())
                        .imageUrls(newPost.getImages().stream().map(PostImage::getImgUrl).collect(Collectors.toList()))
                        .commentCount(newPost.getComments().size())
                        .build())
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }
}
