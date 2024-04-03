package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String toEmail);

    Optional<User> findByKakaoId(String id);

    Optional<User> findByUsername(String kakaoId);
}
