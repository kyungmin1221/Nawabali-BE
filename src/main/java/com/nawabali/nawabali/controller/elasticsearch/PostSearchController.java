//package com.nawabali.nawabali.controller.elasticsearch;
//
//import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
//import com.nawabali.nawabali.service.elasticsearch.PostSearchService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/search")
//public class PostSearchController {
//
//    private final PostSearchService postSearchService;
//
//    @GetMapping("/posts")
//    public List<PostSearch> searchPostsByContents(@RequestParam("query") String query) {
//        return postSearchService.searchByContents(query);
//    }
//}
