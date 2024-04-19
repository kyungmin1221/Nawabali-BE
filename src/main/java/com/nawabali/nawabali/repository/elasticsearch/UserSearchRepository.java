package com.nawabali.nawabali.repository.elasticsearch;

import com.nawabali.nawabali.domain.elasticsearch.UserSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserSearchRepository extends ElasticsearchRepository<UserSearch, Long> {
    List<UserSearch> findByNicknameContaining(String nickname);
    void deleteByNickname(String nickname);
}
