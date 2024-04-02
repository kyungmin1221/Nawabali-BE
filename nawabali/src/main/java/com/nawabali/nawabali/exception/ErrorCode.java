package com.nawabali.nawabali.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // 400 BAD_REQUEST: 잘못된 요청
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    WRONG_USERNAME(BAD_REQUEST, "아이디가 잘못 입력되었습니다."),
    WRONG_PASSWORD(UNAUTHORIZED, "비밀번호가 잘못 입력되었습니다."),
    MISMATCH_PASSWORD(BAD_REQUEST,"두 비밀번호가 일치하지 않습니다."),
    UNVERIFIED_EMAIL(BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    WRONG_MULTIPARTFILE(BAD_REQUEST, "Multipartfile에 문제가 있습니다"),
    WRONG_DTO(BAD_REQUEST,"DTO를 다시 확인해주세요"),
    MISMATCH_ID(BAD_REQUEST,"잘못된 요청입니다."),

    // 401 UNAUTHORIZED: 인증되지 않은 사용자
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "존재하지 않는 회원입니다."),
    UNAUTHORIZED_POST(UNAUTHORIZED, "존재하지 않는 게시물입니다."),
    UNAUTHORIZED_COMMENT(UNAUTHORIZED, "존재하지 않는 댓글입니다."),

    // 403 Forbidden : 클라이언트는 콘텐츠에 접근할 권리를 가지고 있지 않다
    FORBIDDEN_MEMBER(FORBIDDEN,"본인의 게시물이 아닙니다."),

    // 404 NOT_FOUND: 잘못된 리소스 접근
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),
    MEMBER_NOT_FOUND(NOT_FOUND, "해당 회원 정보를 찾을 수 없습니다."),
    POST_NOT_FOUND(NOT_FOUND, "해당 게시물 정보를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(NOT_FOUND, "해당 댓글 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND(NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    PROFILEIMAGE_NOT_FOUND(NOT_FOUND, "해당 프로필이미지를 찾을 수 없습니다."),
    LIKE_NOT_FOUND(NOT_FOUND, "해당 좋아요를 찾을 수 없습니다."),



    // 409 CONFLICT: 중복된 리소스 (요청이 현재 서버 상태와 충돌될 때)
    DUPLICATE_EMAIL(CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(CONFLICT, "이미 존재하는 닉네임입니다."),
    DUPLICATE_FOLDER(CONFLICT, "이미 존재하는 장바구니이름입니다."),
    DUPLICATE_LIKE_TRUE(CONFLICT, "이미 좋아요 되어있습니다."),
    DUPLICATE_LIKE_FALSE(CONFLICT, "이미 좋아요가 취소 되어있습니다."),


    // 500 server error
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "내부 서버 에러입니다.");



    private final HttpStatus httpStatus;
    private final String message;

}
