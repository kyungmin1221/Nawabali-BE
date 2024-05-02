//package com.nawabali.nawabali.postTest;
//
//import com.nawabali.nawabali.constant.Category;
//import com.nawabali.nawabali.constant.Period;
//import com.nawabali.nawabali.domain.Post;
//import com.nawabali.nawabali.dto.PostDto;
//import com.nawabali.nawabali.repository.querydsl.post.PostDslRepositoryCustomImpl;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//
//@SpringBootTest
//class PostDslTest {
//
//
//    @Autowired
//    private PostDslRepositoryCustomImpl postDslRepositoryCustom;
//
//
//
//    // elastic 검색 시간 속도 - 165ms (게시물이 더 많은데도 더 빨리 검색)
////    @Test
////    public void testElasticsearchSearchPerformance() {
////        String searchKeyword = "byby";
////
////        long startTime = System.currentTimeMillis();
////        List<PostSearch> results = postSearchService.searchByContents(searchKeyword);
////        for (PostSearch result : results) {
////            System.out.println("result = " + result.getContents());
////        }
////        long endTime = System.currentTimeMillis();
////
////        System.out.println("Elasticsearch 검색 실행 시간: " + (endTime - startTime) + "ms");
////    }
//
//
//    // query dsl 검색 속도 - 242ms
//    @Test
//    public void testQueryDslSearchPerformance() {
//        String searchKeyword = "byby";
//        String category = "FOOD";
//        String period = "WEEK";
//
//        long startTime = System.currentTimeMillis();
////        List<PostDslDto.SearchDto> results = postDslRepositoryCustom.findSearchByPosts(searchKeyword);
////        for (PostDslDto.SearchDto result : results) {
////            System.out.println("result = " + result.getContents());
////        }
//        long endTime = System.currentTimeMillis();
//
//        System.out.println("QueryDSL 검색 실행 시간: " + (endTime - startTime) + "ms");
//    }
//
//    @Test
//    public void testFindDistrictByPost() {
//        String district = "SUNGBOOK";
//
//        List<PostDto.SortCategoryDto> ct= postDslRepositoryCustom.findCategoryByPost(district);
//
//        for (PostDto.SortCategoryDto sortCategoryDto : ct) {
//            System.out.println("sortCategoryDto = " + sortCategoryDto);
//        }
//
//        // 결과 검증
//        assertNotNull(ct, "구 이름이 null이면 안됩니다.");
//        System.out.println("가장 게시물이 많은 구: " + ct);
//    }
//}
