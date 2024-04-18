package com.nawabali.nawabali.repository;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.domain.Post;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.repository.querydsl.post.PostDslRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , PostDslRepositoryCustom {
    List<PostDto.getMyPostsResponseDto> findByUserId(Long userId);

    Optional<Long> countByTownDistrict(String district);

    @Query("SELECT p.category, COUNT(p.category) FROM Post p WHERE p.town.district = :district GROUP BY p.category ORDER BY COUNT(p.category) DESC")
    List<Object[]> countCategoriesByDistrict(@Param("district") String district);

    default List<Category> findMostFrequentCategoryByTownDistrict(String district) {
        List<Object[]> result = countCategoriesByDistrict(district);

        if (result.isEmpty()) {
            return Collections.emptyList(); // 결과가 비어있으면 빈 리스트를 반환
        }

        List<Category> mostFrequentCategories = new ArrayList<>();
        long maxCount = (long) result.get(0)[1]; // 가장 많은 카테고리의 수

        // 가장 많은 카테고리 수를 가진 카테고리를 모두 찾습니다.
        for (Object[] row : result) {
            long count = (long) row[1];
            if (count == maxCount) {
                mostFrequentCategories.add((Category) row[0]);
            } else {
                // 가장 많은 카테고리 수를 가진 카테고리를 모두 찾았으면 반복문을 종료합니다.
                break;
            }
        }

        return mostFrequentCategories;
    }
}
