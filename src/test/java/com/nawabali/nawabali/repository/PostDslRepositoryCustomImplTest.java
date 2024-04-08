package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostDslRepositoryCustomImplTest {

    @Autowired
    private PostService postService;


    @Test
    public void test1() {
        // 테스트를 위한 PageRequest 생성
        int page = 0;
        int size = 12;
        PageRequest pageRequest = PageRequest.of(page, size);

        // 메서드 실행
        Slice<PostDto.ResponseDto> resultSlice = postService.getPostsByLatest(pageRequest);

        // 결과 검증
        assertNotNull(resultSlice);
        assertFalse(resultSlice.isEmpty());

        // 결과 내용 검증 (옵션)
        resultSlice.getContent().forEach(post -> {
            assertNotNull(post.getTitle());
        });

        // hasNext 검증
        assertEquals(size > resultSlice.getContent().size(), resultSlice.hasNext());
    }
}
