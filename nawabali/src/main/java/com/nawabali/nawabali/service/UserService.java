package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.SignupDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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
                .build();
        userRepository.save(user);
        User responseUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return ResponseEntity.ok(new SignupDto.SignupResponseDto(responseUser.getId()));
    }

    public User getUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    }
}
