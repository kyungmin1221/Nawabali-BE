//package com.nawabali.nawabali.service.elasticsearch;
//
//import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
//import com.nawabali.nawabali.repository.elasticsearch.PostSearchRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PostSearchService {
//
//    private final PostSearchRepository postSearchRepository;
//
//    public List<PostSearch> searchByContents(String contents) {
//        return postSearchRepository.findByContentsContaining(contents);
//    }
//}
//
