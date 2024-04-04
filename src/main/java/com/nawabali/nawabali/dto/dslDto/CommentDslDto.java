package com.nawabali.nawabali.dto.dslDto;

import lombok.*;

public class CommentDslDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto {

        private String contents;

        private String username;
    }
}
