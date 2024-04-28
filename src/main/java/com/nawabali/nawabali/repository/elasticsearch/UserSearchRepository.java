package com.nawabali.nawabali.repository.elasticsearch;

import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
import com.nawabali.nawabali.domain.elasticsearch.UserSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSearchRepository extends ElasticsearchRepository<UserSearch, Long>,CrudRepository<UserSearch, Long> {
    List<UserSearch> findByNicknameContaining(String nickname);

}
