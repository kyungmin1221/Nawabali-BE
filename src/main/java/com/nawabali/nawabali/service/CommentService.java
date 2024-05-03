package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.CommentDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.CommentRepository;
import com.nawabali.nawabali.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
@Slf4j(topic = "CommentService 로그")
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @Transactional
    // 댓글 작성
    public CommentDto.ResponseDto createComment(Long postId, CommentDto.RequestDto dto, Long userId) {

        // 사용자 확인 (로그아웃 됐을 수도 있으니까)
        User user = userService.getUserId(userId);


        // 포스트 아이디를 확인해서 게시물 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        // parent를 null로 초기화
        Comment parent = null;
        Long parentId = dto.getParentId();
        // parentId가 존재할 경우 parentId에 해당하는 Comment 찾아오기
        if (parentId != null) {
            parent = commentRepository.findById(parentId).orElseThrow(()->
                    new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        }

        // 게시물에 댓글을 댓글 repository에 저장하기
        Comment comment = Comment.builder()
                .contents(dto.getContents())
                .user(user)
                .post(post)
                .parent(parent)
                .build();
        commentRepository.save(comment);

//        notificationService.notifyComment(postId);

        // 유저, 게시물, 댓글 관련 자료를 response로 보내기
        return new CommentDto.ResponseDto(comment);
    }

    @Transactional
    // 댓글 수정
    public CommentDto.ResponseDto updateComment(Long commentId, CommentDto.RequestDto dto, Long userId) {

        // 본인 확인
        User user = userService.getUserId(userId);

        // 댓글 가져오기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT);
        }
        // 댓글 내용 수정
        comment.changeContents(dto.getContents());
        commentRepository.save(comment);

        // 값 리턴하기
        return new CommentDto.ResponseDto(comment);
    }

    @Transactional
    // 댓글 삭제하기
    public CommentDto.DeleteResponseDto deleteComment(Long commentId, Long userId) {
        // 본인 확인
        User user = userService.getUserId(userId);

        // 댓글 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT);
        }

        // 댓글 삭제
        commentRepository.delete(comment);


        // commentId랑 메세지 response로 보내기
        return CommentDto.DeleteResponseDto.builder()
                .commentId(commentId)
                .message("댓글이 삭제되었습니다.")
                .build();
    }

    // 댓글 조회(무한 스크롤)
    @Transactional(readOnly = true)
    public Slice<CommentDto.GetResponseDto> getComments(Long postId, Pageable pageable) {
        postRepository.findById(postId).orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        return convertNestedStructure(commentRepository.findCommentsByPostId(postId, pageable));
    }

    private Slice<CommentDto.GetResponseDto> convertNestedStructure(Slice<CommentDto.GetResponseDto> comments) {
        // 중첩 구조로 변환된 댓글 목록을 저장할 리스트입니다.
        List<CommentDto.GetResponseDto> result = new ArrayList<>();
        // 댓글을 부모-자식 구조로 변환하는 데 사용할 맵 : 키는 댓글의 식별자(commentId)이고, 값은 실제 댓글 객체입니다.
        Map<Long, CommentDto.GetResponseDto> map = new HashMap<>();
        // 댓글을 parent comment를 기준으로 순회하여 부모-자식 관계를 형성합니다.
        comments.forEach(dto -> {
            // 현재 댓글을 맵에 추가합니다.
            if (!map.containsKey(dto.getParentId())) {
                map.put(dto.getCommentId(), dto);
                // 부모 댓글인 경우, 결과 목록에 추가합니다.
                if (dto.getParentId() != null) {
                    // 부모 댓글을 찾아 해당 자식 댓글을 추가합니다.
                    map.computeIfAbsent(dto.getParentId(), k -> new CommentDto.GetResponseDto()).getChildren().add(dto);
                } else {
                    // 부모 댓글이 없는 경우: 결과 목록에 바로 추가합니다.
                    result.add(dto);
                }
            }
        });
        // 결과 목록과 페이지 정보를 이용하여 Slice를 생성하여 반환합니다.
        return new SliceImpl<>(result, comments.getPageable(), comments.hasNext());
    }
}