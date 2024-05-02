package com.nawabali.nawabali.repository.elasticsearch;

import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostSearch,Long>, CrudRepository<PostSearch, Long> {
    Page<PostSearch> findByContentsContaining(String contents, Pageable pageable);
    void deleteByPostId(Long postId);
}
