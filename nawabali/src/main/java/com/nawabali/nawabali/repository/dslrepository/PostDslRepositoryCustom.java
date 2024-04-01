package com.nawabali.nawabali.repository.dslrepository;

import com.nawabali.nawabali.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostDslRepositoryCustom {

    Slice<Post> findPostsByLatest(Pageable pageable);

}
