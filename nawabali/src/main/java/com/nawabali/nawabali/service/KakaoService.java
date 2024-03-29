package com.nawabali.nawabali.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.UserDto;
import com.nawabali.nawabali.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "카카오 로그인")
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void kakaoLogin(String code, HttpServletResponse res) throws JsonProcessingException {
        // 1. 인가 코드로 엑세스 토큰 요청
        String accessToken = getToken(code);

        // 2. 엑세스 토큰으로 사용자 정보 가져오기
        UserDto.KakaoUserInfoDto kakaoUserInfoDto = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfoDto);
    }


    private String getToken(String code) throws JsonProcessingException {
        log.info("인가코드로 엑세스 토큰 요청. 인가코드 : " + code);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "f7dda01bb0485f7f9b1a92a21952688e");
        body.add("redirect_uri", "http://localhost:8080/users/kakao/callback");
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String token = jsonNode.get("access_token").asText();
        log.info("kakaoAccessToken: " + token);
        return token;
    }
    private UserDto.KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("카카오 사용자 정보 불러오기");
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
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        System.out.println("id = " + id);

        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        System.out.println("nickname = " + nickname);

        String email = jsonNode.get("kakao_account")
                .get("email").asText();
//        System.out.println("email = " + email);
        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " );
        return new UserDto.KakaoUserInfoDto(id, nickname, email);
    }

    private User registerKakaoUserIfNeeded(UserDto.KakaoUserInfoDto kakaoUserInfoDto) {
        // DB에 기존 카카오 유저 확인
        Long id = kakaoUserInfoDto.getId();
        User kakaoUser = userRepository.findByKakaoId(id);
        if(kakaoUser==null){
            // 카카오 사용자 email 과 동일한 email 있는지 확인
            // 있다면 카카오아이디 업데이트
            String kakaoEmail = kakaoUser.getEmail();
            User sameEmailUser = userRepository.findByEmail(kakaoEmail);
            if(sameEmailUser != null){
                kakaoUser = sameEmailUser;
                kakaoUser.updateKakaoId(id);

            } else{
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email : kakao-email
                String email = kakaoUserInfoDto.getEmail();
                String nickName = kakaoUserInfoDto.getNickname();
                kakaoUser = User.builder()
                        .nickname(nickName)
                        .email(email)
                        .password(encodedPassword)
                        .build();
            }
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }


}
