//package com.nawabali.nawabali.service;
//
//import com.nawabali.nawabali.dto.PostDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class PostSearchTest {
//
//    @Autowired
//    private PostService postService;
//
//    @BeforeEach
//    void setUp() {
//        // 초기화 로직, 테스트 데이터 세팅 등
//    }
//
//    // es -> 139 ms
//    @Test
//    void searchAndFilterPosts() {
//        String contents = "user";
//        int page = 0;
//        int size = 10;
//        Pageable pageable = PageRequest.of(page, size);
//
//        long startTime1 = System.currentTimeMillis();
//        Slice<PostDto.ResponseDto> results = postService.searchAndFilterPosts(contents, pageable);
//        for (PostDto.ResponseDto result : results) {
//            System.out.println("result.getPostId() : " + result.getPostId());
//        }
//        long endTime1 = System.currentTimeMillis();
//
//        System.out.println("메소드 실행시간 : " + (endTime1 - startTime1) + " ms");
//    }
//
//
//    // es + jpa -> 262 ms
//    @Test
//    void searchAndFilterPosts2() {
//        String contents = "user";
//        int page = 0;
//        int size = 10;
//        Pageable pageable = PageRequest.of(page, size);
//
//        long startTime1 = System.currentTimeMillis();
//        Slice<PostDto.ResponseDto> results = postService.searchAndFilterPosts(contents, pageable);
//        System.out.println("results = " + results);
//        long endTime1 = System.currentTimeMillis();
//
//        System.out.println("메소드 실행시간 : " + (endTime1 - startTime1) + " ms");
//    }
//}