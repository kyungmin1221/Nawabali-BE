package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.constant.Address;
import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.domain.Post;
import lombok.*;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestDto {

        private String title;

        private String contents;

        private Category category;

        private Double latitude;

        private Double longitude;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto {

        private Long userId;

        private Long postId;

        private String nickname;

        private String title;

        private String contents;

        private String category;

        private LocalDateTime createdAt;

        private LocalDateTime modifiedAt;

        public ResponseDto(Post post) {
            this.userId = post.getUser().getId();
            this.postId = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.contents = post.getContents();
            this.category = post.getContents();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PatchDto {

        private String title;

        private String contents;

        private Category category;

        private Double latitude;

        private Double longitude;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeleteDto {

        private String message;

    }
}
