package com.nawabali.nawabali.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.exec.spi.JdbcOperationQueryMutationNative;

import java.time.LocalDateTime;

@Slf4j(topic = "CommentDto 로그")
public class CommentDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDto {

        private String contents;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDto{

        private Long postId;
        private Long userId;
        private Long commentId;
        private String nickname;
        private String contents;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String message;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResponseDto{

        private Long commentId;
        private String message;

    }
}

