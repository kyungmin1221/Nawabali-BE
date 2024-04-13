package com.nawabali.nawabali.dto.querydsl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nawabali.nawabali.constant.DeleteStatus;
import com.nawabali.nawabali.domain.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDslDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto {

        private Long commentId;
        private Long postId;
        private Long userId;
        private Long parentId;
        private String nickname;
        private String contents;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
        private List<ResponseDto> children = new ArrayList<>();

        public static CommentDslDto.ResponseDto convertCommentToDto(Comment comment) {
            return comment.getIsDeleted() == DeleteStatus.Y ?
                    new CommentDslDto.ResponseDto(comment, "삭제된 댓글입니다.", null, null, null) :
                    new CommentDslDto.ResponseDto(comment, comment.getContents(), comment.getPost().getId(), comment.getUser().getId(), comment.getUser().getNickname());
        }

        public static List<ResponseDto> convertCommentListToDtoList(List<Comment> comments) {
            List<ResponseDto> responseDtos = new ArrayList<>();
            for (Comment comment : comments) {
                responseDtos.add(convertCommentToDto(comment));
            }
            return responseDtos;
        }

        public ResponseDto(Comment comment, String contents, Long postId, Long userId, String nickname) {
            this.commentId = comment.getId();
            this.contents = contents;
            this.postId = postId;
            this.userId = userId;
            this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
            this.nickname = nickname;
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
            this.children = convertCommentListToDtoList(comment.getChildren());
        }
    }
}
