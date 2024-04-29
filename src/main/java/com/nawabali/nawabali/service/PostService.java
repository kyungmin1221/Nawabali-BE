package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.*;
import com.nawabali.nawabali.domain.BookMark;
import com.nawabali.nawabali.domain.Like;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
import com.nawabali.nawabali.domain.image.PostImage;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.*;
import com.nawabali.nawabali.repository.elasticsearch.PostSearchRepository;
import com.nawabali.nawabali.s3.AwsS3Service;
import com.nawabali.nawabali.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.constant.LikeCategoryEnum.LIKE;
import static com.nawabali.nawabali.constant.LikeCategoryEnum.LOCAL_LIKE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final PostSearchRepository postSearchRepository;
    private final ProfileImageRepository profileImageRepository;
    private final BookMarkRepository bookMarkRepository;
    private final List <String> seoulDistrictNames = Arrays.asList(
            "강남구", "강동구", "강서구", "강북구", "관악구",
            "광진구", "구로구", "금천구", "노원구", "동대문구",
            "도봉구", "동작구", "마포구", "서대문구", "성동구",
            "성북구", "서초구", "송파구", "영등포구", "용산구",
            "양천구", "은평구", "종로구", "중구", "중랑구"
    );


    // 게시물 생성
    @Transactional
    public PostDto.ResponseDto createPost(User user, PostDto.RequestDto requestDto, List<MultipartFile> files) throws IOException {
        User findUser = userService.getUserId(user.getId());

        Town town = new Town(
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getDistrict(),
                requestDto.getPlaceName(),
                requestDto.getPlaceAddr()
        );

        Post post = Post.builder()
                .contents(requestDto.getContents())
                .category(requestDto.getCategory())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .town(town)
                .user(findUser)
                .build();

        // AwsS3Service 변경에 따라 이 부분 수정
        Map<String, Object> uploadResults = awsS3Service.uploadFile(files, "postImages");
        String mainImageUrl = (String) uploadResults.get("mainUrl");
        List<String> originalUrls = (List<String>) uploadResults.get("originalUrls");

        // 메인 이미지 (리사이즈된 이미지) 추가
        PostImage mainImage = PostImage.builder()
                .fileName(mainImageUrl)
                .imgUrl(mainImageUrl)
                .post(post)
                .build();
        post.getImages().add(mainImage);

        // 원본 이미지 URL들을 각각 처리
        originalUrls.forEach(url -> {
            PostImage image = PostImage.builder()
                    .fileName(url)
                    .imgUrl(url)
                    .post(post)
                    .build();
            post.getImages().add(image);
        });

        Post savedPost = postRepository.save(post);
        PostSearch postSearch = createPostSearch(savedPost, originalUrls, findUser);
        postSearchRepository.save(postSearch);

        User userUp = post.getUser();
        if (promoteGrade(userUp)){
            userUp.updateRank(userUp.getRank());
        }

        return new PostDto.ResponseDto(savedPost);
    }


    // 전체 게시물 조회
    public Slice<PostDto.ResponseDto> getPostsByLatest(Pageable pageable) {
        Slice<PostDto.ResponseDto> postSlice = postRepository.findPostsByLatest(pageable);
        List<PostDto.ResponseDto> responseDtos = postSlice.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, postSlice.hasNext());
    }


    // 상세 게시물 조회
    public PostDto.ResponseDetailDto getPost(Long postId, UserDetailsImpl userDetails) {
        Post post = getPostId(postId);
        Long likesCount = getLikesCount(postId, LIKE);
        Long localLikesCount = getLikesCount(postId, LOCAL_LIKE);
        String profileImageUrl = getProfileImage(postId).getImgUrl();

        // 로그인 상태
        if(userDetails!=null){
            // 토글링 여부 확인. DB에 있다면 누른 상태
            Long userId = userDetails.getUser().getId();
            Like like = (Like)likeRepository.findFirstByPostIdAndUserIdAndLikeCategoryEnum(postId, userId, LIKE).orElse(null);
            Like localLike = (Like)likeRepository.findFirstByPostIdAndUserIdAndLikeCategoryEnum(postId, userId, LOCAL_LIKE).orElse(null);
            BookMark bookMark = bookMarkRepository.findByUserIdAndPostId(userId, postId).orElse(null);

            return new PostDto.ResponseDetailDto(
                    post,
                    likesCount,
                    localLikesCount,
                    profileImageUrl,
                    like != null,
                    localLike!=null,
                    bookMark!=null
            );
        }
        // 비 로그인 상태
        return new PostDto.ResponseDetailDto(
                post,
                likesCount,
                localLikesCount,
                profileImageUrl,
                false,
                false,
                false
        );
    }


    // 유저 닉네임으로 그 유저의 게시물들 조회
    public Slice<PostDto.ResponseDto> getUserPost(Long userId, Category category,Pageable pageable) {
        Slice<PostDto.ResponseDto> usersPost = postRepository.getUserPost(userId, category, pageable);
        List<PostDto.ResponseDto> responseDtos = usersPost.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, usersPost.hasNext());
    }


    // 카테고리 별 게시물 조회
    public Slice<PostDto.ResponseDto> getPostByCategory(Category category, String district, Pageable pageable) {
        Slice<PostDto.ResponseDto> postCategory = postRepository.findCategoryByPost(category,district, pageable);
        List<PostDto.ResponseDto> responseDtos = postCategory.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, postCategory.hasNext());
    }


    // 작성된 게시글을 좋아요가 많은 순으로 상위 10개( 카테고리, 구, 기간 으로 필터링 )
    public List<PostDto.ResponseDto> getPostByLike(Category category, String district, Period period) {
        List<PostDto.ResponseDto> posts = postRepository.topLikeByPosts(category,district, period);
        List<PostDto.ResponseDto> responseDtos = posts.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return responseDtos;
    }

    // 각 카테고리 별 최근 한달(일주)간 게시글이 제일 많았던 구 출력
    public PostDto.SortDistrictDto getDistrictByCategory(Category category, Period period) {
        return postRepository.findDistrictByPost(category, period);
    }


    // 구 와 한달기준 각 카테고리의 게시글 개수
    public List<PostDto.SortCategoryDto> getCategoryByPost(String district) {
        return postRepository.findCategoryByPost(district);
    }

    // 게시물 수정 - 사용자 신원 확인
    @Transactional
    public PostDto.PatchDto updatePost(Long postId, User user, PostDto.PatchDto patchDto) {
        Post post = getPostId(postId);
        if(!post.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.FORBIDDEN_MEMBER);
        }

        post.update(patchDto.getContents());
        postRepository.save(post);

        // es 업데이트
        PostSearch existingPostSearch = postSearchRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("es 문서를 찾을 수 없음"));
        existingPostSearch.setContents(post.getContents());

        postSearchRepository.save(existingPostSearch);

        return new PostDto.PatchDto(post);
    }


    // 게시물 삭제
    @Transactional
    public PostDto.DeleteDto deletePost(Long postId) {
        getPostId(postId);
        List<PostImage> images = postImageRepository.findAllByPostId(postId);

        images.forEach(image -> {
            String fullPath = "postImages/" + image.getFileName(); // 폴더 경로를 포함한 전체 경로
            awsS3Service.deleteFile(fullPath);
        });

        postRepository.deleteById(postId);
        postSearchRepository.deleteByPostId(postId);

        return new PostDto.DeleteDto("해당 게시물이 삭제되었습니다.");
    }



    // 게시물 검색(ES)
    public Slice<PostDto.ResponseDto> searchAndFilterPosts(String contents, Pageable pageable) {

        Page<PostSearch> searchResultsPage = postSearchRepository.findByContentsContaining(contents, pageable);

        List<PostDto.ResponseDto> responseDtos = searchResultsPage.getContent().stream()
                .map(this::convertToResponseDtoFromES)
                .collect(Collectors.toList());

        return new SliceImpl<>(responseDtos, pageable, searchResultsPage.hasNext());
    }



    // 동네별 점수 조회
    public List<PostDto.DistrictDto> districtMap() {
        return seoulDistrictNames.stream()
                .map(district -> createDistrictDto(
                        district,
                        countPostsByDistrict(district),
                        countLocalLikeByDistrict(district),
                        getPopularCategoryByDistrict(district)))
                .collect(Collectors.toList());
    }

    // 해당하는 구의 전체 게시물 개수 반환
    private Long countPostsByDistrict(String district) {
        return postRepository.countByTownDistrict(district)
                .orElseThrow(()-> new CustomException(ErrorCode.DISTRICTPOST_NOT_FOUND));
    }

    // 해당하는 구의 전체 지역주민 좋아요 개수 반환
    private Long countLocalLikeByDistrict(String district) {
        return likeRepository.countByPostTownDistrictAndLikeCategoryEnum(district, LOCAL_LIKE)
                .orElseThrow(()-> new CustomException(ErrorCode.DISTRICTLOCALLIKE_NOT_FOUND));
    }

    // 해당하는 구의 인기있는 카테고리 이름 반환
    private Category getPopularCategoryByDistrict(String district) {
        List<Category> categories = postRepository.findMostFrequentCategoryByTownDistrict(district);
        return categories.isEmpty() ? null : categories.get(0);
    }

    private PostDto.DistrictDto createDistrictDto(String district, Long posts, Long localLikes, Category popularCategory) {
        return PostDto.DistrictDto.builder()
                .district(district)
                .totalPost(posts)
                .totalLocalLike(localLikes)
                .popularCategory(popularCategory)
                .build();
    }

    public Long getLikesCount(Long postId, LikeCategoryEnum likeCategoryEnum){
        return likeRepository.countByPostIdAndLikeCategoryEnum(postId, likeCategoryEnum);
    }

    public Post getPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_POST));

    }

    public ProfileImage getProfileImage(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        return profileImageRepository.findByUserId(post.getUser().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILEIMAGE_NOT_FOUND));
    }


    private boolean promoteGrade(User user) {

        List<Post> userPosts = postRepository.findAllByUserId(user.getId());

        List<Long> postIds = userPosts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        Long totalPosts = (long) userPosts.size();

        Long totalLocalLikesCount = userService.getMyTotalLikesCount(postIds, LikeCategoryEnum.LOCAL_LIKE);

        Long needPosts = Math.max(user.getRank().getNeedPosts() - totalPosts, 0L);
        Long needLocalLikes = Math.max(user.getRank().getNeedLikes() - totalLocalLikesCount, 0L);

        return needPosts ==0 && needLocalLikes ==0 && user.getRank() != UserRankEnum.LOCAL_ELDER;
    }


    private PostDto.ResponseDto convertToResponseDto(PostDto.ResponseDto post) {
        Long likesCount = getLikesCount(post.getPostId(), LIKE);
        Long localLikesCount = getLikesCount(post.getPostId(), LikeCategoryEnum.LOCAL_LIKE);
        String profileImageUrl = getProfileImage(post.getPostId()).getImgUrl();

        return new PostDto.ResponseDto(
                post.getUserId(),
                post.getUserRankName(),
                post.getPostId(),
                post.getNickname(),
                post.getContents(),
                post.getCategory(),
                post.getDistrict(),
                post.getPlaceName(),
                post.getPlaceAddr(),
                post.getLatitude(),
                post.getLongitude(),
                post.getCreatedAt(),
                post.getModifiedAt(),
                post.getMainImageUrl(),
                post.getResizedImageUrl(),
                post.isMultiImages(),
                likesCount,
                localLikesCount,
                post.getCommentCount(),
                profileImageUrl
        );
    }


    private PostSearch createPostSearch(Post post, List<String> imageUrls, User user) {
        PostSearch postSearch = new PostSearch();

        postSearch.setId(post.getId().toString());
        postSearch.setContents(post.getContents());
        postSearch.setPostId(post.getId());
        postSearch.setUserId(user.getId());
        postSearch.setUserRankName(user.getRank().getName());
        postSearch.setNickname(user.getNickname());
        postSearch.setCategory(post.getCategory().toString());
        postSearch.setDistrict(post.getTown().getDistrict());
        postSearch.setPlaceName(post.getTown().getPlaceName());
        postSearch.setPlaceAddr(post.getTown().getPlaceAddr());
        postSearch.setLatitude(post.getTown().getLatitude());
        postSearch.setLongitude(post.getTown().getLongitude());
        postSearch.setCreatedAt(LocalDateTime.now());
        postSearch.setModifiedAt(post.getModifiedAt());
        postSearch.setMainImageUrl(imageUrls.isEmpty() ? null : imageUrls.get(0));
        postSearch.setMultiImages(imageUrls.size() > 1);
        postSearch.setLikesCount(0L);
        postSearch.setLocalLikesCount(0L);
        postSearch.setCommentCount(0);
        postSearch.setProfileImageUrl(user.getProfileImage() != null ? user.getProfileImage().getImgUrl() : null);

        return postSearch;
    }


    private PostDto.ResponseDto convertToResponseDtoFromES(PostSearch postSearch) {
        return PostDto.ResponseDto.builder()
                .userId(postSearch.getUserId())
                .userRankName(postSearch.getUserRankName())
                .postId(postSearch.getPostId())
                .nickname(postSearch.getNickname())
                .contents(postSearch.getContents())
                .category(postSearch.getCategory())
                .district(postSearch.getDistrict())
                .placeName(postSearch.getPlaceName())
                .placeAddr(postSearch.getPlaceAddr())
                .latitude(postSearch.getLatitude())
                .longitude(postSearch.getLongitude())
                .createdAt(postSearch.getCreatedAt())
                .modifiedAt(postSearch.getModifiedAt())
                .mainImageUrl(postSearch.getMainImageUrl())
                .multiImages(postSearch.isMultiImages())
                .likesCount(postSearch.getLikesCount())
                .localLikesCount(postSearch.getLocalLikesCount())
                .commentCount(postSearch.getCommentCount())
                .profileImageUrl(postSearch.getProfileImageUrl())
                .build();
    }

}
