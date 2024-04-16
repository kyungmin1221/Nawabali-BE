package com.nawabali.nawabali.dto;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.image.PostImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestDto {

        @NotBlank
        private String contents;

        @NotNull
        private Category category;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        @NotNull
        private String district;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto {       // 게시물 전체 조회 시

        private Long userId;

        private Long postId;

        private String nickname;

        private String contents;

        private String category;

        private String district;

        private Double latitude;

        private Double longitude;

        private LocalDateTime createdAt;

        private LocalDateTime modifiedAt;

        private List<String> imageUrls;

        private Long likesCount;

        private Long localLikesCount;

        private int commentCount;

        private String profileImageUrl;


        public ResponseDto(Post post) {
            this.userId = post.getUser().getId();
            this.postId = post.getId();
            this.nickname = post.getUser().getNickname();
            this.contents = post.getContents();
            this.category = post.getCategory().name();
            this.district = post.getTown().getDistrict();
            this.latitude = post.getTown().getLatitude();
            this.longitude = post.getTown().getLongitude();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
            this.imageUrls = post.getImages().stream()
                    .map(PostImage::getImgUrl)
                    .collect(Collectors.toList());
            this.commentCount = post.getComments().size();

        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDetailDto {     // 게시물 상세 조회 시

        private Long userId;

        private Long postId;

        private String nickname;

        private String contents;

        private String category;

        private LocalDateTime createdAt;

        private LocalDateTime modifiedAt;

        private List<String> imageUrls;

        private int commentCount;

        private Long likesCount;

        private Long localLikesCount;

        private String profileImageUrl;

        private String district;

        private boolean likeStatus;
        private boolean localLikeStatus;
        private boolean bookmarkStatus;


        public ResponseDetailDto(Post post, Long likesCount, Long localLikesCount, String profileImageUrl, boolean likeStatus, boolean localLikesStatus, boolean bookmarkStatus) {
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
            this.profileImageUrl = profileImageUrl;
            this.district = post.getTown().getDistrict();
            this.likeStatus = likeStatus;
            this.localLikeStatus = localLikesStatus;
            this.bookmarkStatus = bookmarkStatus;
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PatchDto {

        private String contents;

        public PatchDto(Post post) {
            this.contents = post.getContents();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeleteDto {

        private String message;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class getMyPostsResponseDto {

        private Long id;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DistrictDto {

        private String district;
        private Long totalPost;
//        private Long totalLike;
        private Long totalLocalLike;

    }
}
