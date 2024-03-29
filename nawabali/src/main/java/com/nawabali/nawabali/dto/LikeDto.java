package com.nawabali.nawabali.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j (topic = "LikeDto 로그")
public class LikeDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class requestDto {

        private boolean status;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class responseDto {

        private Long likeId;
        private Long postId;
        private Long userId;
        private boolean status;
        private String message;

    }

}