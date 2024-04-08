package com.nawabali.nawabali.dto.dslDto;

import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.image.PostImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostDslDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto {

        private Long userId;

        private Long postId;

        private String nickname;

        private String contents;

        private String category;

        private LocalDateTime createdAt;

        private LocalDateTime modifiedAt;

        private List<String> imageUrls;

        private Long likesCount;

        private Long localLikesCount;

        private int commentCount;

        public ResponseDto(Post post, Long likesCount, Long localLikesCount) {
            this.userId = post.getUser().getId();
            this.postId = post.getId();
            this.nickname = post.getUser().getNickname();
            this.contents = post.getContents();
            this.category = post.getCategory().name();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
            this.imageUrls = post.getImages().stream()
                    .map(PostImage::getImgUrl)
                    .collect(Collectors.toList());
            this.commentCount = post.getComments().size();
            this.likesCount = likesCount;
            this.localLikesCount = localLikesCount;
        }

    }
}
