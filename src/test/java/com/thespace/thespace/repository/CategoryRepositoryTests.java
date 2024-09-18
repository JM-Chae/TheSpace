package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.service.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@SpringBootTest
@Log4j2
public class CategoryRepositoryTests
  {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommunityRepository communityRepository;

    @Test
    public void createCateTest()
      {
        Optional<Community> result = communityRepository.findById(1L);
        Community community =  result.orElseThrow();

        Category category = Category.builder().community(community)
            .categoryName("open forum")
            .path(result.get().getCommunityName())
            .categoryType("open forum")
            .build();

         Long categoryId = categoryRepository.save(category).getCategoryId();

         log.info(categoryId);
      }
  }