package com.nawabali.nawabali.dto.querydsl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nawabali.nawabali.constant.UserRankEnum.*;

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
        private String profileImageUrl;
        private String userInfo;
        private List<ResponseDto> children = new ArrayList<>();

        public ResponseDto(Comment comment) {
            this.commentId = comment.getId();
            this.contents = comment.getContents();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUser().getId();
            this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
            this.nickname = comment.getUser().getNickname();
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
            this.profileImageUrl = comment.getUser().getProfileImage().getImgUrl();
            this.userInfo = comment.getUser().getAddress().getDistrict()+" "+RankInKorean(comment);
            this.children = comment.getChildren().stream()
                    .map(ResponseDto::new)
                    .collect(Collectors.toList());
        }

        public String RankInKorean(Comment comment) {
            UserRankEnum rank = comment.getUser().getRank();
            return switch (rank) {
                case RESIDENT -> "주민";
                case NATIVE_PERSON -> "토박이";
                case LOCAL_ELDER -> "터줏대감";
                default -> throw new CustomException(ErrorCode.RANK_NOT_FOUND);
            };
        }
    }
}
