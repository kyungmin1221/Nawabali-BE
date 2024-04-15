package com.nawabali.nawabali.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nawabali.nawabali.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "CommentDto 로그")
public class CommentDto implements Serializable {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDto {

        private String contents;
        private Long parentId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDto{

        private Long commentId;
        private Long postId;
        private Long userId;
        private String nickname;
        private String contents;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
        private List<ResponseDto> children = new ArrayList<>();

        public ResponseDto(Comment comment) {
            this.commentId = comment.getId();
            this.contents = comment.getContents();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUser().getId();
            this.nickname = comment.getUser().getNickname();
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailResponseDto{

        private Long postId;
        private Long userId;
        private Long commentId;
        private String nickname;
        private String contents;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
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

