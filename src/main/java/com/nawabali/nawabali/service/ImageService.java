package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.s3.AwsS3Service;
import com.nawabali.nawabali.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j(topic = "profile 이미지 로그")
public class ImageService {

    private final AwsS3Service awsS3Service;
    private final ProfileImageRepository profileImageRepository;

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
