package com.nawabali.nawabali.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.domain.elasticsearch.UserSearch;
import com.nawabali.nawabali.domain.image.ProfileImage;
import com.nawabali.nawabali.dto.KakaoDto;
import com.nawabali.nawabali.global.tool.redis.RedisTool;
import com.nawabali.nawabali.repository.ProfileImageRepository;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.repository.elasticsearch.UserSearchRepository;
import com.nawabali.nawabali.security.Jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTool redisTool;
    private final RestTemplate restTemplate;
    private final UserSearchRepository userSearchRepository;
    private final ProfileImageRepository profileImageRepository;

    private final String local= "http://localhost:8080/api/user/kakao/callback";
    private final String frontLocal = "http://localhost:3000/api/user/kakao/callback";
    private final String develop = "https://hhboard.shop/api/user/kakao/callback";
    private final String domain = "https://www.dongnaebangnae.com/api/user/kakao/callback";

    private final String release = "https://prod.dongnaebangnae.com/api/user/kakao/callback";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Transactional
    public String kakaoLogin(String code , HttpServletResponse response) throws JsonProcessingException, IOException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code, release);

        // 2. 필요시에 회원가입 및 위치 정보(address 값) 저장
        User kakaoUser = registerKakaoUserIfNeeded(accessToken);
        log.info("userinfo : " + kakaoUser.getNickname());
        log.info("userinfo : " + kakaoUser.getEmail());

        // 3. 로그인 JWT 토큰 발행 및 리프레시 토큰 저장
        return jwtTokenCreate(kakaoUser,response);
    }

    // 토큰을 요청하고 카카오 서버에서 토큰을 발급 받음- post요청
    private String getAccessToken(String code, String redirect_uri) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 수정된 회원 가입 및 위치 정보 저장 로직
    private User registerKakaoUserIfNeeded(String accessToken) throws JsonProcessingException {
        KakaoDto.userInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        Long kakaoId = kakaoUserInfo.getId();

        // DB 에 중복된 Kakao Email 가 있는지 확인
        String kakaoEmail = kakaoUserInfo.getEmail();
        User kakaoUser = userRepository.findByEmail(kakaoEmail).orElse(null);

        if (kakaoUser == null) {

            String kakaoNickname = kakaoUserInfo.getNickname(); // 카카오 사용자 닉네임
            String password = passwordEncoder.encode(UUID.randomUUID().toString());

            UserRoleEnum role = UserRoleEnum.USER; // 기본 역할을 ROLE_USER로 설정
            Address address =new Address("수정해주세요", "수정해주세요");
            kakaoUser = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(kakaoNickname)
                    .email(kakaoEmail)
                    .address(address)
                    .password(password)
                    .role(role)
                    .rank(UserRankEnum.RESIDENT)
                    .build();

            kakaoUser = userRepository.save(kakaoUser);
            ProfileImage profileImage = new ProfileImage(kakaoUser);
            profileImageRepository.save(profileImage);
            kakaoUser.updateProfileImage(profileImage);
            UserSearch userSearch = new UserSearch(kakaoUser, profileImage.getImgUrl());

            userSearchRepository.save(userSearch);

        }
        else{
            kakaoUser.updateKakaoId(kakaoId);
            userRepository.save(kakaoUser);
        }


        return kakaoUser;
    }


    // JWT 토큰 생성 및 리프레시 토큰 저장(레디스) 로직
    private String jwtTokenCreate(User kakaoUser , HttpServletResponse response) throws IOException {
        String email = kakaoUser.getEmail();
        UserRoleEnum role = kakaoUser.getRole();

        String token = jwtUtil.createAccessToken(email, role);
        log.info("token : " + token);
        Cookie accessCookie = jwtUtil.createAccessCookie(token);
        Cookie refreshCookie = jwtUtil.createRefreshCookie(email);

        log.info("user email : " + email, role);
        log.info("accessCookie value : " + accessCookie.getValue());
        log.info("refreshCookie value : " + refreshCookie.getValue());
        // 6. 헤더 및 쿠키에 저장
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        response.addCookie(accessCookie);

        // 7. refresh 토큰 redis에 저장
        // substring 제외
//        redisTool.setValues(token.substring(7),
        redisTool.setValues(token,
                        refreshCookie.getValue(),
                Duration.ofMillis(jwtUtil.REFRESH_EXPIRATION_TIME));

        return token;
    }



    // 사용자 정보 받아오기
    private KakaoDto.userInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("accessToken : " + accessToken);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RequestEntity<?> requestEntity = RequestEntity
                .get(uri)
                .headers(headers)
                .build();

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String properties = jsonNode.toString();
        log.info("---------");
        log.info("properties : " + properties);
        log.info("---------");
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.path("kakao_account").path("email").asText();

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoDto.userInfoDto(id, nickname, email);
    }


}
