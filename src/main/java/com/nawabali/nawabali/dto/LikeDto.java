package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class LikeDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class requestDto {

        private LikeCategoryEnum likeCategoryEnum;
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
        private LikeCategoryEnum likeCategoryEnum;
        private boolean status;
        private String message;

    }

}