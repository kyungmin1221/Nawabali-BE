package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.*;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.elasticsearch.UserSearch;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.global.tool.redis.RedisTool;
import com.nawabali.nawabali.repository.LikeRepository;
import com.nawabali.nawabali.repository.PostRepository;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.repository.elasticsearch.UserSearchRepository;
import com.nawabali.nawabali.security.Jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j(topic = "UserService")
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageRepository profileImageRepository;


    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserSearchRepository userSearchRepository;
    private final JwtUtil jwtUtil;
    private final RedisTool redisTool;

    public ResponseEntity<String> logout(String accessToken, HttpServletResponse response) {
        if (StringUtils.hasText(accessToken)) {
            log.info("accessToken : " + accessToken);
            accessToken = accessToken.substring(7);
            String refreshToken = redisTool.getValues(accessToken);
            if (!refreshToken.equals("false")) {
                log.info("refreshToken 삭제.  key = " + accessToken);
                redisTool.deleteValues(accessToken);

                //access의 남은 유효시간만큼  redis에 블랙리스트로 저장
                log.info("redis에 블랙리스트 저장");
                Long remainedExpiration = jwtUtil.getUserInfoFromToken(accessToken).getExpiration().getTime();
                Long now = new Date().getTime();
                if (remainedExpiration > now) {
                    long newExpiration = remainedExpiration - now;
                    redisTool.setValues(accessToken, "logout", Duration.ofMillis(newExpiration));
                }
            }
        }
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, null);
        return ResponseEntity.ok(accessToken);
    }

    @Transactional
    public ResponseEntity<SignupDto.SignupResponseDto> signup(SignupDto.SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String rawPassword = requestDto.getPassword();

        // 비밀번호 일치 검증
        if (!rawPassword.equals(requestDto.getConfirmPassword())) {
            throw new CustomException(ErrorCode.MISMATCH_PASSWORD);

        }

        String password = passwordEncoder.encode(rawPassword);

        // 관리자 권한 부여
        UserRoleEnum role = UserRoleEnum.USER;
//        if(requestDto.isAdmin()){
//            role = UserRoleEnum.ADMIN;
//        }

        // 프론트엔드로부터 받은 주소 정보를 사용하여 Address 객체 생성
        Address address = new Address(
                requestDto.getCity(),
                requestDto.getDistrict()
        );

        User user = User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .role(role)
                .address(address)
                .rank(UserRankEnum.RESIDENT)
                .oauthStatus(false)
                .build();

        ProfileImage profileImage = new ProfileImage(user);

        userRepository.save(user);
        profileImageRepository.save(profileImage);
        User responseUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        UserSearch userSearch = new UserSearch(responseUser, profileImage.getImgUrl());
        userSearchRepository.save(userSearch);

        return ResponseEntity.ok(new SignupDto.SignupResponseDto(responseUser.getId()));
    }


    public UserDto.UserInfoResponseDto getUserInfo(User user) {
        User existUser = getUserId(user.getId());
        Long userId = existUser.getId();

        // 유저 아이디로 작성된 postID 모두 검색
        List<Long> postIds = getMyPostIds(userId);
        System.out.println("postIds = " + postIds);

        // 작성된 postID로  게시글, 좋아요, 로컬좋아요 카운팅
        int totalPostsCount = postIds.size();
        Long totalLikesCount = getMyTotalLikesCount(postIds, LikeCategoryEnum.LIKE);
        Long totalLocalLikesCount = getMyTotalLikesCount(postIds, LikeCategoryEnum.LOCAL_LIKE);

        Long needPosts = Math.max(existUser.getRank().getNeedPosts() - totalPostsCount, 0L);
        Long needLocalLikes = Math.max(existUser.getRank().getNeedLikes() - totalLocalLikesCount, 0L);

        postIds = null;

        return UserDto.UserInfoResponseDto.builder()
                .id(existUser.getId())
                .email(existUser.getEmail())
                .nickname(existUser.getNickname())
                .rankName(existUser.getRank().getName())
                .city(existUser.getAddress().getCity())
                .district(existUser.getAddress().getDistrict())
                .totalPostsCount(totalPostsCount)
                .totalLikesCount(totalLikesCount)
                .totalLocalLikesCount(totalLocalLikesCount)
                .profileImageUrl(existUser.getProfileImage().getImgUrl())
                .needPosts(needPosts)
                .needLikes(needLocalLikes)
                .oauthStatus(existUser.isOauthStatus())
                .build();
    }

    @Transactional
    public UserDto.UserInfoResponseDto updateUserInfo(User user, UserDto.UserInfoRequestDto requestDto) {
        User existUser = getUserId(user.getId());

        // 도시, 구 검증
        String cityName = requestDto.getCity();
        String districtName = requestDto.getDistrict();
        if (!CityEnum.checkCorrectAddress(cityName, districtName)) {
            throw new CustomException(ErrorCode.INVALID_DISTRICT_NAME);
        }

        if (StringUtils.hasText(requestDto.getPassword())) {
            // 비밀번호 일치 검증
            if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
                throw new CustomException(ErrorCode.MISMATCH_PASSWORD);
            }
            String password = passwordEncoder.encode(requestDto.getPassword());
            requestDto.setPassword(password);

        } else {
            requestDto.setPassword(existUser.getPassword());
        }
        existUser.update(requestDto);

        UserSearch userSearch = new UserSearch(existUser, existUser.getProfileImage().getImgUrl());
        userSearchRepository.save(userSearch);
        return new UserDto.UserInfoResponseDto(existUser);
    }

    @Transactional
    public ResponseEntity<UserDto.deleteResponseDto> deleteUserInfo(User user) {
        User existUser = getUserId(user.getId());

        userRepository.delete(existUser);
        userSearchRepository.deleteById(existUser.getId());
        return ResponseEntity.ok(new UserDto.deleteResponseDto());
    }

    public Slice<PostDto.ResponseDto> getMyPosts(User user, Pageable pageable, Category category) {
        User existUser = getUserId(user.getId());
        Long userId = existUser.getId();
        Slice<PostDto.ResponseDto> rawPosts = postRepository.getMyPosts(userId, pageable, category);
        List<PostDto.ResponseDto> posts = rawPosts.getContent().stream()
                .map(responseDto -> {
                    Long likeCount = getLikesCount(responseDto.getPostId(), LikeCategoryEnum.LIKE);
                    Long localLikeCount = getLikesCount(responseDto.getPostId(), LikeCategoryEnum.LOCAL_LIKE);

                    responseDto.setLikesCount(likeCount);
                    responseDto.setLocalLikesCount(localLikeCount);

                    return responseDto;
                })
                .toList();

        return new SliceImpl<>(posts, pageable, rawPosts.hasNext());
    }

    public List<UserSearch> searchNickname(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return null;
        }
        return userSearchRepository.findByNicknameContaining(nickname);
    }

    // 메서드 //

    public boolean checkNickname(String nickname) {
        User duplicatedUser = userRepository.findByNickname(nickname);
        return duplicatedUser == null;
    }

    public boolean checkMyPassword(String inputPassword, User user) {
        User existUser = getUserId(user.getId());
        String myPassword = existUser.getPassword();
        return passwordEncoder.matches(inputPassword, myPassword);

    }

    public User getUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Long getMyTotalLikesCount(List<Long> postIds, LikeCategoryEnum likeCategoryEnum) {
        return likeRepository.countByPostIdInAndLikeCategoryEnum(postIds, likeCategoryEnum);
    }

    public Long getLikesCount(Long postId, LikeCategoryEnum likeCategoryEnum) {
        return likeRepository.countByPostIdAndLikeCategoryEnum(postId, likeCategoryEnum);
    }

    public List<Long> getMyPostIds(Long userId) {
        return postRepository.findByUserId(userId).stream()
                .map(PostDto.getMyPostsResponseDto::getId)
                .toList();
    }

}
