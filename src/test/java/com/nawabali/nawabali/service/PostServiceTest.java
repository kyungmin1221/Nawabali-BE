package com.nawabali.nawabali.service;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Period;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.repository.PostRepository;
import com.nawabali.nawabali.repository.elasticsearch.PostSearchRepository;
import com.nawabali.nawabali.repository.querydsl.post.PostDslRepositoryCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // mockito 사용
class PostServiceTest {


    @Mock   // mock 객체 생성
    PostRepository postRepository;



    @InjectMocks    // @Mock 이 붙은 목객체를 이 어노테이션이 붙은 객체에 주입할 수 있음
    private PostService postService;


    @Test
    @DisplayName("최신순으로 모든 게시물 불러오기")
    void getPostsByLatest() {

    }


    @Test
    @DisplayName("상세 게시물 불러오기")
    void getPost() {
    }

    @Test
    @DisplayName("카테고리 별 게시물 조회하기")
    void getPostByCategory() {
    }


    @Test
    @DisplayName("좋아요가 많은 순으로 게시물을 10개 조회")
    void getPostByLike() {
    }

    @Test
    @DisplayName("각 카테고리 별 기간기준 게시글이 가장 많았던 구를 출력")
    void getDistrictByCategory() {

        // given
        Category testCategory = Category.PHOTOZONE;
        Period testPeriod = Period.MONTH;

        // when
        PostDto.SortDistrictDto mockResponse = new PostDto.SortDistrictDto("SUNGBOOK", 3L);
        when(postRepository.findDistrictByPost(testCategory, testPeriod)).thenReturn(mockResponse);

        PostDto.SortDistrictDto result = postService.getDistrictByCategory(testCategory, testPeriod);
        System.out.println("가장 많았던 구 = " + result.getDistrict());
        System.out.println("게시물 개수 = " + result.getPostCount());

        // then
        assertNotNull(result);
        assertEquals("SUNGBOOK", result.getDistrict(),"테스트 미통과");
        assertEquals(3L, result.getPostCount());

    }

    @Test
    @DisplayName("구와 한달기준 각 카테고리의 게시글 개수 출력")
    void getCategoryByPost() {

        String testDistrict = "SUNGBOOK";
        List<PostDto.SortCategoryDto> mockResponse = new ArrayList<>();
        mockResponse.add(new PostDto.SortCategoryDto("SUNGBOOK", 3L));

        when(postRepository.findCategoryByPost(testDistrict)).thenReturn(mockResponse);

        List<PostDto.SortCategoryDto> result = postService.getCategoryByPost(testDistrict);
        for (PostDto.SortCategoryDto sortCategoryDto : result) {
            System.out.println("sortCategoryDto.getCategory() = " + sortCategoryDto.getCategory());
            System.out.println("sortCategoryDto.getPostCount() = " + sortCategoryDto.getPostCount());
            
        }

        assertNotNull(result);
        System.out.println("result.size() = " + result.size());     //1
        System.out.println("Category.values().length = " + Category.values().length);   //3
        assertTrue(result.size() <= Category.values().length);
        assertTrue(result.stream().anyMatch(r -> r.getCategory().equals("SUNGBOOK") && r.getPostCount() == 3L));
    }
}