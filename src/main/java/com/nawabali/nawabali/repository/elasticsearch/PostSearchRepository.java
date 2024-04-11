package com.nawabali.nawabali.repository.elasticsearch;

import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostSearch,Long> , CrudRepository<PostSearch,Long> {
    List<PostSearch> findByContentsContaining(String contents);
    void deleteByPostId(Long postId);

}
