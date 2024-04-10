package com.nawabali.nawabali.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nawabali.nawabali.constant.DeleteStatus;
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
        private Long postId;
        private Long userId;
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

        public ResponseDto(Comment comment, String contents, Long postId, Long userId, String nickname) {
            this.commentId = comment.getId();
            this.contents = contents;
            this.postId = postId;
            this.userId = userId;
            this.nickname = nickname;
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
        }

        public static ResponseDto convertCommentToDto(Comment comment) {
            return comment.getIsDeleted() == DeleteStatus.Y ?
                    new ResponseDto(comment, "삭제된 댓글입니다.", null, null, null) :
                    new ResponseDto(comment, comment.getContents(), comment.getPost().getId(), comment.getUser().getId(), comment.getUser().getNickname());
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

