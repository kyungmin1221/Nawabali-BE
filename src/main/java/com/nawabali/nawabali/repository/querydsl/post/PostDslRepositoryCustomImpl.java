package com.nawabali.nawabali.repository.querydsl.post;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.QPost;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.domain.image.PostImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.querydsl.PostDslDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.domain.QPost.post;
import static com.nawabali.nawabali.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class PostDslRepositoryCustomImpl implements PostDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public PostDslRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    // 게시물 전체 조회 시 무한 스크롤
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
                        .contents(newPost.getContents())
                        .category(newPost.getCategory().name())
                        .district(newPost.getTown().getDistrict())
                        .createdAt(newPost.getCreatedAt())
                        .modifiedAt(newPost.getModifiedAt())
                        .imageUrls(newPost.getImages().stream().map(PostImage::getImgUrl).collect(Collectors.toList()))
                        .commentCount(newPost.getComments().size())
                        .build())
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }


    // 게시물 contents 로 검색
    @Override
    public List<PostDslDto.SearchDto> findSearchByPosts(String contents)  {
        QPost post = QPost.post;

        List<PostDslDto.SearchDto> searchResults = queryFactory
                .select(Projections.constructor(PostDslDto.SearchDto.class,
                        post.id,
                        post.contents))
                .from(post)
                .where(post.contents.contains(contents))
                .fetch();

        return searchResults;
    }

    @Override
    public Slice<PostDslDto.ResponseDto> findCategoryByPost(String category, String district, Pageable pageable) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(categoryEq(category) , districtEq(district))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();

        if (hasNext) {
            posts.remove(posts.size() - 1);
        }

        List<PostDslDto.ResponseDto> responseDtos = posts.stream()
                .map(newPost -> PostDslDto.ResponseDto.builder()
                        .userId(newPost.getUser().getId())
                        .postId(newPost.getId())
                        .nickname(newPost.getUser().getNickname())
                        .contents(newPost.getContents())
                        .category(newPost.getCategory().name())
                        .district(newPost.getTown().getDistrict())
                        .createdAt(newPost.getCreatedAt())
                        .modifiedAt(newPost.getModifiedAt())
                        .imageUrls(newPost.getImages().stream().map(PostImage::getImgUrl).collect(Collectors.toList()))
                        .commentCount(newPost.getComments().size())
                        .build())
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }

    @Override
    public Slice<Post> getMyPosts(Long userId, Pageable pageable, Category category) {
        List<Post> posts= queryFactory
                .selectFrom(post)
                .where(post.user.id.eq(userId),
                        categoryEq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .orderBy(post.createdAt.desc())
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();
        if(hasNext){
            posts.remove(posts.size() -1);
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    // Category 조건
    private BooleanExpression categoryEq(String category) {
        return hasText(category) ? post.category.eq(Category.valueOf(category)) : null;
    }

    private BooleanExpression categoryEq(Category category) {
        if(category==null){
            return null;
        }else{
            return post.category.eq(category);
        }
    }

    // District 조건
    private BooleanExpression districtEq(String district) {
        return hasText(district) ? post.town.district.eq(district) : null;
    }
}
