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

        private Long bookmarkId;

        private Long userId;
        
        public ResponseDto(BookMark bookMark) {
            this.bookmarkId = bookMark.getId();
            this.userId = bookMark.getUser().getId();
        }
    }
}
