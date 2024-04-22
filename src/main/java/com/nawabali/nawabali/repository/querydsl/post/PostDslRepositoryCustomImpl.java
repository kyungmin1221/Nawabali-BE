package com.nawabali.nawabali.repository.querydsl.post;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Period;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.QLike;
import com.nawabali.nawabali.domain.QPost;
import com.nawabali.nawabali.domain.QUser;
import com.nawabali.nawabali.domain.image.PostImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.querydsl.core.Tuple;
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

import static com.nawabali.nawabali.constant.LikeCategoryEnum.LIKE;
import static com.nawabali.nawabali.domain.QPost.post;
import static com.nawabali.nawabali.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class PostDslRepositoryCustomImpl implements PostDslRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    // 게시물 전체 조회 시 무한 스크롤
    @Override
    public Slice<PostDto.ResponseDto> findPostsByLatest(Pageable pageable) {
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

        List<PostDto.ResponseDto> responseDtos = convertPostDto(posts);
        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }


    // 게시물 contents 로 검색
    @Override
    public List<PostDto.SearchDto> findSearchByPosts(String contents)  {
        QPost post = QPost.post;

        List<PostDto.SearchDto> searchResults = queryFactory
                .select(Projections.constructor(PostDto.SearchDto.class,
                        post.id,
                        post.contents))
                .from(post)
                .where(post.contents.contains(contents))
                .fetch();

        return searchResults;
    }

    // 카테고리 및 구 를 이용하여 게시물 조회
    @Override
    public Slice<PostDto.ResponseDto> findCategoryByPost(Category category, String district, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(
                        categoryEq(category),
                        districtEq(district)
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();

        if (hasNext) {
            posts.remove(posts.size() - 1);
        }

        List<PostDto.ResponseDto> responseDtos = convertPostDto(posts);

        return new SliceImpl<>(responseDtos, pageable, hasNext);
    }

    // 일주일 동안 좋아요 기준 상위 10개 게시물 조회
    @Override
    public List<PostDto.ResponseDto> topLikeByPosts(Category category, String district, Period period) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QLike like = QLike.like;


        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.user,user)
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

        List<PostDto.ResponseDto> responseDtos = convertPostDto(posts);
        return responseDtos;

    }

    // 각 카테고리 별 최근 한달(일주)간 게시글이 제일 많았던 구 및 게시글 수 출력
    @Override
    public PostDto.SortDistrictDto findDistrictByPost(Category category, Period period) {
        QPost post = QPost.post;

        PostDto.SortDistrictDto results = queryFactory
                .select(Projections.bean(PostDto.SortDistrictDto.class,
                        post.town.district,
                        post.count().as("postCount")))
                .from(post)
                .where(categoryEq(category),
                        periodEq(period))
                .groupBy(post.town.district)
                .orderBy(post.count().desc())
                .limit(1)
                .fetchOne();

        return results;
    }

    // 구 와 한달기준 각 카테고리의 게시글 개수
    @Override
    public List<PostDto.SortCategoryDto> findCategoryByPost(String district) {
        QPost post = QPost.post;
        LocalDateTime oneMonth = LocalDateTime.now().minusMonths(1);

        List<PostDto.SortCategoryDto> results = queryFactory
                .select(Projections.bean(PostDto.SortCategoryDto.class,
                        post.category.stringValue().as("category"),
                        post.count().as("postCount"))
                )
                .from(post)
                .where(
                        districtEq(district),
                        post.createdAt.after(oneMonth)
                )
                .groupBy(post.category)
                .fetch();

        // 정의된 모든 카테고리를 확인하고 빠진 카테고리에 대해 결과 추가
        for (Category category : Category.values()) {
            if (results.stream().noneMatch(r -> r.getCategory().equals(category.name()))) {
                results.add(new PostDto.SortCategoryDto(category.name(), 0L));
            }
        }
        
        return results;

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


    private List<PostDto.ResponseDto> convertPostDto(List<Post> posts) {
        return posts.stream()
                .map(post -> PostDto.ResponseDto.builder()
                        .userId(post.getUser().getId())
                        .userRankName(post.getUser().getRank().getName())
                        .postId(post.getId())
                        .nickname(post.getUser().getNickname())
                        .contents(post.getContents())
                        .category(post.getCategory().name())
                        .district(post.getTown().getDistrict())
                        .placeName(post.getTown().getPlaceName())
                        .placeAddr(post.getTown().getPlaceAddr())
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
