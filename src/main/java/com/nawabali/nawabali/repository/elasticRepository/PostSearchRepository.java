package com.nawabali.nawabali.repository.elasticRepository;

import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.domain.elastic.PostSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostSearch,Long> {
    List<PostSearch> findByContentsContaining(String contents);
    void deleteByPostId(Long postId);

}
