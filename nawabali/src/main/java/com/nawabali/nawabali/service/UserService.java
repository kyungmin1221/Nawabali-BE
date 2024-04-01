package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.Address;
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
    public SignupDto.SignupResponseDto signup(SignupDto.SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String rawPassword = requestDto.getPassword();

        // 비밀번호 일치 검증
        if(!rawPassword.equals(requestDto.getConfirmPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String password = passwordEncoder.encode(rawPassword);

        boolean certificated = requestDto.isCertificated();
        if(!certificated){
            throw new IllegalArgumentException("이메일 인증을 진행해주세요.");
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
                .build();
        userRepository.save(user);
        User responseUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return new SignupDto.SignupResponseDto(responseUser.getId());
    }

    public User getUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
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
}
