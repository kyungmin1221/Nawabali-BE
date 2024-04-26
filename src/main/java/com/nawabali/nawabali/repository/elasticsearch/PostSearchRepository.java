package com.nawabali.nawabali.repository.elasticsearch;

import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
import com.nawabali.nawabali.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostSearch,Long> {
    Page<PostSearch> findByContentsContaining(String contents, Pageable pageable);
//    List<PostSearch> findByContentsContaining(String contents);
    void deleteByPostId(Long postId);
}
