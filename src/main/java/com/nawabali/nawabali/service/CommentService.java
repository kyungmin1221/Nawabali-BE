package com.nawabali.nawabali.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
@Slf4j(topic = "CommentService 로그")
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    public CommentDto.ResponseDto createComment(Long postId, CommentDto.RequestDto dto, String username) {

        // 사용자 확인 (로그아웃 됐을수도 있으니까)
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 포스트 아이디를 확인해서 게시물 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 게시물에 댓글을 댓글 repository에 저장하기
        Comment comment = Comment.builder()
                .contents(dto.getContents())
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);

        // 유저, 게시물, 댓글 관련 자료를 response로 보내기
        return CommentDto.ResponseDto.builder()
                .userId(user.getId())
                .postId(postId)
                .commentId(comment.getId())
                .nickname(user.getNickname())
                .contents(comment.getContents())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .message("댓글이 작성되었습니다.")
                .build();
    }

    // 댓글 수정
    public CommentDto.ResponseDto updateComment(Long commentId, CommentDto.RequestDto dto, String username) {

        // 본인 확인
        if (username ==  null){
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        } log.info("본인확인" + username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));


        // 댓글 가져오기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        log.info("댓글 가져오기" + commentId);

        // 본인이 쓴 댓글인지 확인
        if (comment.getUser() == null || !comment.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT);
        }
        // 댓글 entity에 넣고 저장

        comment = comment.toBuilder()
                .contents(dto.getContents())
                .build();
        commentRepository.save(comment);

        // 값 리턴하기
        return CommentDto.ResponseDto.builder()
                .userId(user.getId())
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .nickname(user.getNickname())
                .contents(comment.getContents())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .message("댓글이 수정되었습니다.")
                .build();
    }

    // 댓글 삭제하기
    public CommentDto.DeleteResponseDto deleteComment(Long commentId, String username) {
        // 본인 확인
        if (username ==  null){
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 댓글 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (comment.getUser() == null || !comment.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT);
        }

        // 삭제하기
        commentRepository.delete(comment);

        // id랑 메세지 response로 보내기
        return CommentDto.DeleteResponseDto.builder()
                .commentId(commentId)
                .message("댓글이 삭제되었습니다.")
                .build();
    }

    // 댓글 조회(무한 스크롤)
    public Slice<CommentDslDto.ResponseDto> getComments(Long postId, Pageable pageable) {
        return commentRepository.findCommentsByPostId(postId , pageable);
    }
}
