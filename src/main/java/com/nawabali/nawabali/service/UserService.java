package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.LikeCategoryEnum;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.repository.LikeRepository;
import com.nawabali.nawabali.repository.PostRepository;
import com.nawabali.nawabali.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageRepository profileImageRepository;


    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
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
                .build();

        ProfileImage profileImage = new ProfileImage(user);

        userRepository.save(user);
        profileImageRepository.save(profileImage);
        User responseUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return ResponseEntity.ok(new SignupDto.SignupResponseDto(responseUser.getId()));
    }


    public UserDto.UserInfoResponseDto getUserInfo(User user) {
        User existUser = getUserId(user.getId());

        // 유저 아이디로 작성된 postID 모두 검색
        List<Long> postIds = postRepository.findByUserId(user.getId()).stream()
                .map(PostDto.getMyPostsResponseDto::getId)
                .toList();
        System.out.println("postIds = " + postIds);

        // 작성된 postID로 좋아요, 로컬좋아요 카운팅
        Long totalLikeCount = getMyTotalLikesCount(postIds, LikeCategoryEnum.LIKE);
        Long totalLocalLikeCount = getMyTotalLikesCount(postIds, LikeCategoryEnum.LOCAL_LIKE);


        return UserDto.UserInfoResponseDto.builder()
                .id(existUser.getId())
                .email(existUser.getEmail())
                .nickname(existUser.getNickname())
                .rank(existUser.getRank())
                .city(existUser.getAddress().getCity())
                .district(existUser.getAddress().getDistrict())
                .totalLikesCount(totalLikeCount)
                .totalLocalLikesCount(totalLocalLikeCount)
                .build();
    }

    @Transactional
    public UserDto.UserInfoResponseDto updateUserInfo(User user, UserDto.UserInfoRequestDto requestDto) {
        User existUser = getUserId(user.getId());

        String password = passwordEncoder.encode(requestDto.getPassword());
        requestDto.setPassword(password);

        existUser.update(requestDto);
        return new UserDto.UserInfoResponseDto(existUser);
    }

    @Transactional
    public ResponseEntity<UserDto.deleteResponseDto> deleteUserInfo(User user) {
        User existUser = getUserId(user.getId());

        userRepository.delete(existUser);
        return ResponseEntity.ok(new UserDto.deleteResponseDto());
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

    public Long getMyTotalLikesCount(List<Long> postIds, LikeCategoryEnum likeCategoryEnum){
        Long total =0L;
        for (Long postId : postIds){
            Long numLikes = likeRepository.countByPostIdAndLikeCategoryEnum(postId, likeCategoryEnum);
            total += numLikes;
        }
        return total;
    }


}
