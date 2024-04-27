package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.LikeCategoryEnum;
import com.nawabali.nawabali.constant.UserRankEnum;
import com.nawabali.nawabali.domain.Like;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.LikeDto;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.LikeRepository;
import com.nawabali.nawabali.repository.PostRepository;
import com.nawabali.nawabali.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j (topic = "LikeService 로그")
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    // 좋아요 수정
    @Transactional
    public LikeDto.responseDto toggleLike(Long postId, String username) {

        // 본인 인증
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 게시물 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 해당 게시물에 좋아요를 눌렀는지 확인
        Like findLike = likeRepository.findByUserIdAndPostIdAndLikeCategoryEnum(user.getId(), postId, LikeCategoryEnum.LIKE);

        if (findLike != null && findLike.isStatus()){
            // 좋아요를 이미 눌렀다면, 좋아요 내역 삭제
            likeRepository.delete(findLike);
            // response 보내기
            return LikeDto.responseDto.builder()
                    .likeId(findLike.getId())
                    .userId(user.getId())
                    .postId(postId)
                    .likeCategoryEnum(findLike.getLikeCategoryEnum())
                    .status(findLike.isStatus())
                    .message("좋아요 취소 되었습니다.")
                    .build();
        }  else{
            // 내역에 없디면 추가.
            findLike = Like.builder()
                    .user(user)
                    .post(post)
                    .likeCategoryEnum(LikeCategoryEnum.LIKE)
                    .status(true)
                    .build();

            likeRepository.save(findLike);

//            notificationService.notifyLike(postId,user.getId());

        }
        // response 보내기
        return LikeDto.responseDto.builder()
                .likeId(findLike.getId())
                .userId(user.getId())
                .postId(postId)
                .likeCategoryEnum(findLike.getLikeCategoryEnum())
                .status(findLike.isStatus())
                .message("좋아요 되었습니다.")
                .build();
    }
    public LikeDto.responseDto toggleLocalLike(Long postId, String username) {
        // 본인 인증
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 게시물 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 해당 지역의 회원인지 확인
        if(!isMatchDistrict(user, post)){
            throw new CustomException(ErrorCode.MISMATCH_ADDRESS);
        }

        // 해당 게시물에 로컬좋아요를 눌렀는지 확인
        Like findLocalLike = likeRepository.findByUserIdAndPostIdAndLikeCategoryEnum(user.getId(), postId, LikeCategoryEnum.LOCAL_LIKE);
        if(findLocalLike != null && findLocalLike.isStatus()){
            // 누른 상태라면 삭제
            likeRepository.delete(findLocalLike);

            return LikeDto.responseDto.builder()
                    .likeId(findLocalLike.getId())
                    .userId(user.getId())
                    .postId(postId)
                    .likeCategoryEnum(findLocalLike.getLikeCategoryEnum())
                    .status(findLocalLike.isStatus())
                    .message("동네 좋아요 취소 되었습니다.")
                    .build();
        }

        else{
            // 내역에 없디면 추가.
            findLocalLike = Like.builder()
                    .user(user)
                    .post(post)
                    .likeCategoryEnum(LikeCategoryEnum.LOCAL_LIKE)
                    .status(true)
                    .build();

            likeRepository.save(findLocalLike);

            User writer = post.getUser();
            if (promoteGrade(writer)){
                log.info(writer.getRank().getName());
                writer.updateRank(writer.getRank());
            }

//            notificationService.notifyLocalLike(postId, user.getId());

            return LikeDto.responseDto.builder()
                    .likeId(findLocalLike.getId())
                    .userId(user.getId())
                    .postId(postId)
                    .likeCategoryEnum(findLocalLike.getLikeCategoryEnum())
                    .status(findLocalLike.isStatus())
                    .message("동네 좋아요 되었습니다.")
                    .build();
        }
    }

    private boolean isMatchDistrict(User user, Post post){
        String userAddress = user.getAddress().getDistrict();
        String postAddress = post.getTown().getDistrict();

        return userAddress.equals(postAddress);
    }

    public boolean promoteGrade(User writer){
        Long writerId = writer.getId();
        User existUser = userRepository.findById(writerId).orElseThrow(()->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        // 유저 아이디로 작성된 postID 모두 검색
        List<Long> postIds = getMyPostIds(writerId);
        System.out.println("postIds = " + postIds);

        // 작성된 postID로  게시글, 좋아요, 로컬좋아요 카운팅
        int totalPostsCount = postIds.size();
        Long totalLocalLikesCount = getMyTotalLikesCount(postIds, LikeCategoryEnum.LOCAL_LIKE);

        Long needPosts = Math.max(existUser.getRank().getNeedPosts() - totalPostsCount, 0L);
        Long needLocalLikes = Math.max(existUser.getRank().getNeedLikes() - totalLocalLikesCount, 0L);
        log.info("남은 동네 좋아요 수 : " + needLocalLikes);
        log.info("남은 게시글 수 :" + needPosts);
        return needPosts ==0 && needLocalLikes ==0 && existUser.getRank() != UserRankEnum.LOCAL_ELDER;
    }

    public Long getMyTotalLikesCount(List<Long> postIds, LikeCategoryEnum likeCategoryEnum) {
        return likeRepository.countByPostIdInAndLikeCategoryEnum(postIds, likeCategoryEnum);
    }

    public List<Long> getMyPostIds(Long userId) {
        return postRepository.findByUserId(userId).stream()
                .map(PostDto.getMyPostsResponseDto::getId)
                .toList();
    }

}