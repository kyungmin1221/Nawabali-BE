package com.nawabali.nawabali.repository.querydsl.post;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Period;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.QLike;
import com.nawabali.nawabali.domain.QPost;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.domain.image.PostImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.querydsl.PostDslDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.domain.QPost.post;
import static com.nawabali.nawabali.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class PostDslRepositoryCustomImpl implements PostDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;


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

        List<PostDslDto.ResponseDto> responseDtos = convertPostDto(posts);
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

    // 카테고리 및 구 를 이용하여 게시물 조회
    @Override
    public Slice<PostDslDto.ResponseDto> findCategoryByPost(Category category, String district, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;

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

        List<PostDslDto.ResponseDto> responseDtos = convertPostDto(posts);
        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }

    // 일주일 동안 좋아요 기준 상위 10개 게시물 조회
    @Override
    public List<PostDslDto.ResponseDto> topLikeByPosts(Category category, String district, Period period) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QLike like = QLike.like;


        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.user,user).fetchJoin()
                .leftJoin(post.likes,like)
                .where(
                        periodEq(period),
                        categoryEq(category),
                        districtEq(district)
                )
                .groupBy(post.id)
                .orderBy(
                        like.count().desc(),
                        post.createdAt.desc()
                )
                .limit(10)
                .fetch();

        List<PostDslDto.ResponseDto> responseDtos = convertPostDto(posts);
        return responseDtos;

    }

    @Override
    public Slice<PostDto.ResponseDto> getMyPosts(Long userId, Pageable pageable, Category category) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts= queryFactory
                .selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(
                        userEq(userId),
                        categoryEq(category)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .orderBy(post.createdAt.desc())
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();
        if(hasNext){
            posts.remove(posts.size() -1);
        }

        return new SliceImpl<>(posts, pageable, hasNext).map(PostDto.ResponseDto::new);
    }


    // District 조건
    private BooleanExpression districtEq(String district) {
        return hasText(district) ? post.town.district.eq(district) : null;
    }

    // Category 조건
    private BooleanExpression categoryEq(Category category) {
        if(category==null){
            return null;
        }else{
            return post.category.eq(category);
        }
    }

    // 일주일 또는 한달 기간 조건
    private BooleanExpression periodEq(Period period) {
        if(period == null) {
            return null;
        }

        LocalDateTime end  = LocalDateTime.now();
        LocalDateTime start = switch (period) {
            case WEEK -> end.minusWeeks(1);
            case MONTH -> end.minusMonths(1);
            default -> throw new CustomException(ErrorCode.PERIOD_NOT_FOUND);
        };

        return post.createdAt.between(start, end);
    }

    // user 조건
    private BooleanExpression userEq(Long userId){
        if(userId ==null){
            return null;
        }else{
            return post.user.id.eq(userId);
        }
    }

    private List<PostDslDto.ResponseDto> convertPostDto(List<Post> posts) {
        return posts.stream()
                .map(post -> PostDslDto.ResponseDto.builder()
                        .userId(post.getUser().getId())
                        .postId(post.getId())
                        .nickname(post.getUser().getNickname())
                        .contents(post.getContents())
                        .category(post.getCategory().name())
                        .district(post.getTown().getDistrict())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .imageUrls(post.getImages().stream().map(PostImage::getImgUrl).collect(Collectors.toList()))
                        .commentCount(post.getComments().size())
                        .latitude(post.getTown().getLatitude())
                        .longitude(post.getTown().getLongitude())
                        .build())
                .collect(Collectors.toList());
    }


}
