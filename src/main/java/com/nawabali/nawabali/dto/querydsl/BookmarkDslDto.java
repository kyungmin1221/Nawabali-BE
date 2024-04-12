package com.nawabali.nawabali.dto.querydsl;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class BookmarkDslDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserBookmarkDto  {

        private Long userId;

        private Long postId;

        private String nickname;

        private String contents;

        private String category;

        private String district;

        private Double latitude;

        private Double longitude;

        private LocalDateTime createdAt;

        private List<String> imageUrls;

        private Long likesCount;

        private Long localLikesCount;

        private int commentCount;

    }
}
