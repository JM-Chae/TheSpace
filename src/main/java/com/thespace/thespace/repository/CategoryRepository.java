package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>
  {
    @Query("select c.categoryName from Category c where c.community = :community")
    List<String> findCategoryNameByCommunity(@Param("community") Community community);
    List<Category> findByPath(@Param("path") String path);
  }
