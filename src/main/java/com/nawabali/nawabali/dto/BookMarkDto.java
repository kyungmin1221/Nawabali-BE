package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.domain.BookMark;
import lombok.*;

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
    public static class UserBookmarkDto {

        private Long bookmarkId;

        private Long postId;

        private Long userId;

    }
}
