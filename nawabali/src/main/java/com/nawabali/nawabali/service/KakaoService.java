package com.nawabali.nawabali.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.UserRoleEnum;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.KakaoDto;
import com.nawabali.nawabali.global.tool.redis.RedisTool;
import com.nawabali.nawabali.repository.UserRepository;
import com.nawabali.nawabali.security.Jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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


    @Transactional
    public KakaoDto.signupResponseDto kakaoLogin(String code, KakaoDto.addressRequestDto requestDto) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code, "http://localhost:8080/api/user/kakao/callback");

        // 2. 필요시에 회원가입 및 위치 정보(address 값) 저장
        User kakaoUser = registerKakaoUserIfNeeded(accessToken, requestDto);

        // 3. 로그인 JWT 토큰 발행 및 리프레시 토큰 저장
        Map<String, String> tokens = jwtTokenCreate(kakaoUser);

        // 클라이언트에 전달할 응답 생성
        return KakaoDto.signupResponseDto.builder()
                .userId(kakaoUser.getId())
                .accessToken(tokens.get("accessToken"))
                .refreshToken(tokens.get("refreshToken"))
                .build();
    }

    // 토큰을 요청하고 카카오 서버에서 토큰을 발급 받음- post요청
    private String getAccessToken(String code, String redirect_uri) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "52329a0a112266267bafd3864529e810");
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
    private User registerKakaoUserIfNeeded(String accessToken, KakaoDto.addressRequestDto requestDto) throws JsonProcessingException {
        KakaoDto.userInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // DB 에 중복된 Kakao Id 가 있는지 확인
        String kakaoId = String.valueOf(kakaoUserInfo.getId());
        User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) {
            String kakaoEmail = kakaoUserInfo.getEmail(); // 카카오 사용자 이메일
            String kakaoNickname = kakaoUserInfo.getNickname(); // 카카오 사용자 닉네임
            String password = passwordEncoder.encode(UUID.randomUUID().toString());

            UserRoleEnum role = UserRoleEnum.USER; // 기본 역할을 ROLE_USER로 설정
            Address address = new Address(
                    requestDto.getCity(),
                    requestDto.getDistrict()
            );

            kakaoUser = User.builder()
                    .username(kakaoId)
                    .nickname(kakaoNickname)
                    .email(kakaoEmail)
                    .password(password)
                    .role(role)
                    .address(address) // 위치 정보
                    .build();
            userRepository.save(kakaoUser);
        }

        return kakaoUser;
    }


    // JWT 토큰 생성 및 리프레시 토큰 저장(레디스) 로직
    private Map<String, String> jwtTokenCreate(User kakaoUser) {
        String accessToken = jwtUtil.createAccessToken(kakaoUser.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(kakaoUser.getUsername());

        // 리프레시 토큰 Redis에 저장
        redisTool.setValues(kakaoUser.getUsername(), refreshToken, Duration.ofDays(30));

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
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
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.path("kakao_account").path("email").asText(); // path()는 값이 없을 경우 null을 반환하지 않고, "missing node"를 반환합니다.

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoDto.userInfoDto(id, nickname, email);
    }


}
