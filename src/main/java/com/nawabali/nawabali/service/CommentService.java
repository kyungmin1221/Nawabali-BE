package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.DeleteStatus;
import com.nawabali.nawabali.domain.Comment;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.CommentDto;
import com.nawabali.nawabali.dto.dslDto.CommentDslDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.CommentRepository;
import com.nawabali.nawabali.repository.PostRepository;
import com.nawabali.nawabali.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    // 댓글 작성
    public CommentDto.ResponseDto createComment(Long postId, CommentDto.RequestDto dto, String username) {

        // 사용자 확인 (로그아웃 됐을 수도 있으니까)
        User user = userService.getUserId(dto.getUserId());


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

        // 유저, 게시물, 댓글 관련 자료를 response로 보내기
        return CommentDto.ResponseDto.convertCommentToDto(comment);
    }

    @Transactional
    // 댓글 수정
    public CommentDto.ResponseDto updateComment(Long commentId, CommentDto.RequestDto dto, String username) {

        // 본인 확인
        User user = userService.getUserId(dto.getUserId());
        log.info("본인확인" + username);

        // 댓글 가져오기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        log.info("댓글 가져오기" + commentId);

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT);
        }
        // 댓글 내용 수정
        comment.changeContents(dto.getContents());
        commentRepository.save(comment);

        // 값 리턴하기
        return CommentDto.ResponseDto.convertCommentToDto(comment);
    }

    @Transactional
    // 댓글 삭제하기
    public CommentDto.DeleteResponseDto deleteComment(Long commentId, String username) {
        // 본인 확인
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 댓글 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT);
        }

        // 댓글 삭제
        // 자식 댓글이 존재한다면 soft delete
        if (comment.getChildren().size() != 0) {
            comment.changeDeletedStatus(DeleteStatus.Y);
        } else {
            // 자식 댓글이 없다면 hard delete
            commentRepository.delete(getDeletableAncestorComment(comment));
        }

        // id랑 메세지 response로 보내기
        return CommentDto.DeleteResponseDto.builder()
                .commentId(commentId)
                .message("댓글이 삭제되었습니다.")
                .build();
    }

    private Comment getDeletableAncestorComment(Comment comment) {
        Comment parent = comment.getParent();
        if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted() == DeleteStatus.Y)
                return getDeletableAncestorComment(parent);
        return comment;
    }

    // 댓글 조회(무한 스크롤)
    @Transactional(readOnly = true)
    public Slice<CommentDslDto.ResponseDto> getComments(Long postId, Pageable pageable) {
        postRepository.findById(postId).orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        return convertNestedStructure(commentRepository.findCommentsByPostId(postId, pageable));
    }

    private Slice<CommentDslDto.ResponseDto> convertNestedStructure(Slice<CommentDslDto.ResponseDto> comments) {
        List<CommentDslDto.ResponseDto> result = new ArrayList<>();
        Map<Long, CommentDslDto.ResponseDto> map = new HashMap<>();
        comments.stream().forEach(dto -> {
            if (!map.containsKey(dto.getParentId())) {
                map.put(dto.getCommentId(), dto);
                if (dto.getParentId() != null) {
                    map.get(dto.getParentId()).getChildren().add(dto);
                } else {
                    result.add(dto);
                }
            }
        });
        return new SliceImpl<>(result, comments.getPageable(), comments.hasNext());
    }
}
