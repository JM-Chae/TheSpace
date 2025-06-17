package com.thespace.spaceweb.category;

import com.thespace.spaceweb.community.Community;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c.categoryName from Category c where c.community = :community")
    List<String> findCategoryNameByCommunity(@Param("community") Community community);

    List<Category> findByPath(@Param("path") String path);

    Category findByCategoryName(@Param("categoryName") String categoryName);
}
