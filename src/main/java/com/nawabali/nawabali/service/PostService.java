package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import com.nawabali.nawabali.constant.Town;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
import com.nawabali.nawabali.domain.image.PostImage;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.querydsl.PostDslDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.LikeRepository;
import com.nawabali.nawabali.repository.PostImageRepository;
import com.nawabali.nawabali.repository.PostRepository;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.repository.elasticsearch.PostSearchRepository;
import com.nawabali.nawabali.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.constant.LikeCategoryEnum.LIKE;
import static com.nawabali.nawabali.constant.LikeCategoryEnum.LOCAL_LIKE;

@Service
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


    // 게시물 생성
    @Transactional
    public PostDto.ResponseDto createPost(User user, PostDto.RequestDto requestDto, List<MultipartFile> files) {
        User findUser = userService.getUserId(user.getId());

        Town town = new Town(
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getDistrict()
        );

        Post post = Post.builder()
                .contents(requestDto.getContents())
                .category(requestDto.getCategory())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .town(town)
                .user(findUser)
                .build();

        List<String> imageUrls = awsS3Service.uploadFile(files, "postImages");
        imageUrls.forEach(url -> {
            PostImage image = new PostImage();
            image.setFileName(url);
            image.setImgUrl(url);
            post.addImage(image);
        });

        Post savedPost = postRepository.save(post);
        PostSearch postSearch = new PostSearch();
        postSearch.setContents(savedPost.getContents());
        postSearch.setPostId(savedPost.getId());

        postSearchRepository.save(postSearch);

        return new PostDto.ResponseDto(post);

    }

    // 전체 게시물 조회
    public Slice<PostDto.ResponseDto> getPostsByLatest(Pageable pageable) {
        Slice<PostDslDto.ResponseDto> postSlice = postRepository.findPostsByLatest(pageable);
        List<PostDto.ResponseDto> content = postSlice.getContent().stream()
                .map(post -> {
                    Long likesCount = getLikesCount(post.getPostId(), LIKE);
                    Long localLikesCount = getLikesCount(post.getPostId(), LikeCategoryEnum.LOCAL_LIKE);

                    return new PostDto.ResponseDto(
                            post.getUserId(),
                            post.getPostId(),
                            post.getNickname(),
                            post.getContents(),
                            post.getCategory(),
                            post.getDistrict(),
                            post.getLatitude(),
                            post.getLongitude(),
                            post.getCreatedAt(),
                            post.getModifiedAt(),
                            post.getImageUrls(),
                            likesCount,
                            localLikesCount,
                            post.getCommentCount()
                    );
                })
                .collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, postSlice.hasNext());
    }


    // 상세 게시물 조회
    public PostDto.ResponseDetailDto getPost(Long postId) {
        Post post = getPostId(postId);
        Long likesCount = getLikesCount(postId, LIKE);
        Long localLikesCount = getLikesCount(postId, LikeCategoryEnum.LOCAL_LIKE);
        String profileImageUrl = getProfileImage(postId).getImgUrl();

        return new PostDto.ResponseDetailDto(post, likesCount, localLikesCount, profileImageUrl);
    }

    // 카테고리 별 게시물 조회
    public Slice<PostDto.ResponseDto> getPostByCategory(String category, String district, Pageable pageable) {
        Slice<PostDslDto.ResponseDto> postCategory = postRepository.findCategoryByPost(category,district, pageable);
        List<PostDto.ResponseDto> content = postCategory.getContent().stream()
                .map(post -> {
                    Long likesCount = getLikesCount(post.getPostId(), LIKE);
                    Long localLikesCount = getLikesCount(post.getPostId(), LikeCategoryEnum.LOCAL_LIKE);

                    return new PostDto.ResponseDto(
                            post.getUserId(),
                            post.getPostId(),
                            post.getNickname(),
                            post.getContents(),
                            post.getCategory(),
                            post.getDistrict(),
                            post.getLatitude(),
                            post.getLongitude(),
                            post.getCreatedAt(),
                            post.getModifiedAt(),
                            post.getImageUrls(),
                            likesCount,
                            localLikesCount,
                            post.getCommentCount()
                    );
                })
                .collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, postCategory.hasNext());
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

        return new PostDto.PatchDto(post);
    }


    // 게시물 삭제
    @Transactional
    public PostDto.DeleteDto deletePost(Long postId) {
        getPostId(postId);
        List<PostImage> images = postImageRepository.findAllByPostId(postId);

        // AWS S3에서 각 이미지 삭제
        images.forEach(image -> {
            String fullPath = "postImages/" + image.getFileName(); // 폴더 경로를 포함한 전체 경로
            awsS3Service.deleteFile(fullPath);
        });

        postRepository.deleteById(postId);
        postSearchRepository.deleteByPostId(postId);

        return new PostDto.DeleteDto("해당 게시물이 삭제되었습니다.");
    }


    // 게시물 검색 (es)
    public List<PostSearch> searchByContents(String contents) {
        return postSearchRepository.findByContentsContaining(contents);
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

    // 동네별 점수 조회
    public List<PostDto.DistrictDto> districtMap() {

        List <String> seoulDistrictNames = Arrays.asList(
                "강남구", "강동구", "강서구", "강북구", "관악구",
                "광진구", "구로구", "금천구", "노원구", "동대문구",
                "도봉구", "동작구", "마포구", "서대문구", "성동구",
                "성북구", "서초구", "송파구", "영등포구", "용산구",
                "양천구", "은평구", "종로구", "중구", "중랑구"
        );

        List <PostDto.DistrictDto> districtDtoList = new ArrayList<>();

        for (String district : seoulDistrictNames) {

            Long post = postRepository.countByTownDistrict(district)
                    .orElseThrow(()-> new CustomException(ErrorCode.DISTRICTPOST_NOT_FOUND));

            Long like = likeRepository.countByPostTownDistrictAndLikeCategoryEnum(district, LIKE)
                    .orElseThrow(()-> new CustomException(ErrorCode.DISTRICTLIKE_NOT_FOUND));

            Long localLike = likeRepository.countByPostTownDistrictAndLikeCategoryEnum(district, LOCAL_LIKE)
                    .orElseThrow(()-> new CustomException(ErrorCode.DISTRICTLOCALLIKE_NOT_FOUND));

            PostDto.DistrictDto districtDto = PostDto.DistrictDto.builder()
                    .district(district)
                    .totalPost(post)
//                    .totalLike(like)
                    .totalLocalLike(localLike)
                    .build();

            districtDtoList.add(districtDto);
        }

        return districtDtoList;
    }

}
