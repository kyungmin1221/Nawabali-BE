package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.repository.dslrepository.PostDslRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , PostDslRepositoryCustom {
}
