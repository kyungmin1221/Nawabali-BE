package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.DefaultProfileImage;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.elasticsearch.UserSearch;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.repository.elasticsearch.UserSearchRepository;
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
    private final UserSearchRepository userSearchRepository;

    @Transactional
    public UserDto.ProfileImageDto updateProfileImage(UserDetailsImpl userDetails, MultipartFile multipartFile) {
        if(userDetails==null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        User user = userDetails.getUser();

        String url = awsS3Service.uploadSingleFile(multipartFile, "profileImage");

        ProfileImage profileImage = profileImageRepository.findById(user.getProfileImage().getId()).orElseThrow(()->
                new CustomException(ErrorCode.PROFILEIMAGE_NOT_FOUND));
        profileImage.updateFileName(multipartFile.getOriginalFilename());
        profileImage.updateImgUrl(url);
        profileImageRepository.save(profileImage);
        // 프로필 사진 변경 시 엘라스틱 서치에 반영
        UserSearch userSearch = new UserSearch(user, url);
        userSearchRepository.save(userSearch);

        return new UserDto.ProfileImageDto(profileImage);
    }

    @Transactional
    public UserDto.DeleteDto deleteProfileImage(UserDetailsImpl userDetails) {
        if(userDetails==null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        User user = userDetails.getUser();

        ProfileImage profileImage = profileImageRepository.findById(user.getProfileImage().getId()).orElseThrow(()->
                new CustomException(ErrorCode.PROFILEIMAGE_NOT_FOUND));
        // Default 프로필이미지로 변경
        profileImage.updateFileName(DefaultProfileImage.fileName);
        profileImage.updateImgUrl(DefaultProfileImage.imgUrl);
        profileImageRepository.save(profileImage);

        // 프로필 사진 변경 시 엘라스틱 서치에 반영
        UserSearch userSearch = new UserSearch(user, DefaultProfileImage.imgUrl);
        userSearchRepository.save(userSearch);

        return new UserDto.DeleteDto("프로필사진이 삭제되었습니다.");
    }

}
