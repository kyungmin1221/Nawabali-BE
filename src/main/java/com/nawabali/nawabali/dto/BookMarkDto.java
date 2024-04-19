package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.domain.BookMark;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class BookMarkDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto {

        private boolean status;

        private Long bookmarkId;

        private Long postId;

        private Long userId;

        private LocalDateTime createdAt;

        public ResponseDto(BookMark bookMark) {
            this.status = bookMark.isStatus();
            this.bookmarkId = bookMark.getId();
            this.postId = bookMark.getPost().getId();
            this.userId = bookMark.getUser().getId();
        }

    }

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
