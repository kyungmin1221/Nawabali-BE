package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.s3.AwsS3Service;
import com.nawabali.nawabali.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final ProfileImageRepository profileImageRepository;

    @Transactional
    public ResponseEntity<SignupDto.SignupResponseDto> signup(SignupDto.SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String rawPassword = requestDto.getPassword();

        // 비밀번호 일치 검증
        if(!rawPassword.equals(requestDto.getConfirmPassword())){
            throw new CustomException(ErrorCode.MISMATCH_PASSWORD);
//            CustomException customException = new CustomException(ErrorCode.MISMATCH_PASSWORD);
//            return new ResponseEntity<>(customException, customException.getErrorCode().getHttpStatus());
        }

        String password = passwordEncoder.encode(rawPassword);

        boolean certificated = requestDto.isCertificated();
        if(!certificated){
            throw new CustomException(ErrorCode.UNVERIFIED_EMAIL);
        }
        // 관리자 권한 부여
        UserRoleEnum role = UserRoleEnum.USER;
        if(requestDto.isAdmin()){
            role = UserRoleEnum.ADMIN;
        }

        // 프론트엔드로부터 받은 주소 정보를 사용하여 Address 객체 생성
        Address address = new Address(
                requestDto.getCity(),
                requestDto.getDistrict()
        );

        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .password(password)
                .role(role)
                .address(address)
                .rank(UserRankEnum.RESIDENT)
                .build();
        userRepository.save(user);
        User responseUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return ResponseEntity.ok(new SignupDto.SignupResponseDto(responseUser.getId()));
    }


    @Transactional
    public UserDto.ProfileImageDto createProfileImage(Long userId, UserDetailsImpl userDetails, MultipartFile multipartFile) {
        User user = userDetails.getUser();
        if(!userId.equals(user.getId())){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        String url = awsS3Service.uploadSingleFile(multipartFile, "profileImage");
        ProfileImage profileImage = ProfileImage.builder()
                .fileName(multipartFile.getOriginalFilename())
                .imgUrl(url)
                .user(user)
                .build();
        profileImageRepository.save(profileImage);
        return new UserDto.ProfileImageDto(profileImage);
    }

    @Transactional
    public UserDto.ProfileImageDto updateProfileImage(Long userId, UserDetailsImpl userDetails, MultipartFile multipartFile) {
        User user = userDetails.getUser();
        if(!userId.equals(user.getId())){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        String url = awsS3Service.uploadSingleFile(multipartFile, "profileImage");

        ProfileImage profileImage = profileImageRepository.findById(user.getProfileImage().getId()).orElseThrow(()->
                new CustomException(ErrorCode.PROFILEIMAGE_NOT_FOUND));
        profileImage.updateFileName(multipartFile.getOriginalFilename());
        profileImage.updateImgUrl(url);
        profileImageRepository.save(profileImage);

        return new UserDto.ProfileImageDto(profileImage);
    }

    @Transactional
    public UserDto.DeleteDto deleteProfileImage(Long userId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if(!userId.equals(user.getId())){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        ProfileImage profileImage = profileImageRepository.findById(user.getProfileImage().getId()).orElseThrow(()->
                new CustomException(ErrorCode.PROFILEIMAGE_NOT_FOUND));
        profileImageRepository.delete(profileImage);
        return new UserDto.DeleteDto("프로필사진이 삭제되었습니다.");
    }

    @Transactional
    public UserDto.UserInfoResponseDto getUserInfo(Long userId, User user) {

        if (isMatchUserId(userId, user)) {

            Long localCount = 1L; // 물어볼것, 수정해야할 부분
            Long likesCount = 1L;

            return UserDto.UserInfoResponseDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .rank(user.getRank())
                    .city(user.getAddress().getCity())
                    .district(user.getAddress().getDistrict())
                    .localCount(localCount)
                    .likesCount(likesCount)
                    .build();
        }
        throw new CustomException(ErrorCode.MISMATCH_ID);
    }
    @Transactional
    public UserDto.UserInfoResponseDto updateUserInfo(Long userId, User user, UserDto.UserInfoRequestDto requestDto) {
        if(isMatchUserId(userId, user)){
            user.update(requestDto);
            return new UserDto.UserInfoResponseDto(user);
        }
        throw new CustomException(ErrorCode.MISMATCH_ID);
    }

    @Transactional
    public ResponseEntity<UserDto.deleteResponseDto> deleteUserInfo(Long userId, User user) {
        if(isMatchUserId(userId, user)){
            userRepository.delete(user);
            return ResponseEntity.ok(new UserDto.deleteResponseDto());
        }
        throw new CustomException(ErrorCode.MISMATCH_ID);
    }

    public boolean isMatchUserId(Long userId, User user){
        User existUser = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        Long existUserId = existUser.getId();
        Long detailsUserId = user.getId();
        return existUserId.equals(detailsUserId);
    }

    public User getUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
