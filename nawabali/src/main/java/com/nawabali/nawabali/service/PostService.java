package com.nawabali.nawabali.service;

import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import com.nawabali.nawabali.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public PostDto.ResponseDto createPost(User user, PostDto.RequestDto requestDto) {
        User findUser = userService.getUserId(user.getId());

        Post post = Post.builder()
                .title(requestDto.getTitle())
                .contents(requestDto.getContents())
                .category(requestDto.getCategory())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .user(findUser)
                .build();

        postRepository.save(post);

        return new PostDto.ResponseDto(post);

    }

    public Post getPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_POST));

    }


}
