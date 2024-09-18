package com.thespace.thespace.service;

import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.CategoryDTO;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService
  {
    private CategoryRepository categoryRepository;
    private CommunityRepository communityRepository;

    @Autowired
    public void setCommunityRepository(CommunityRepository communityRepository)
      {
        this.communityRepository = communityRepository;
      }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository)
      {
        this.categoryRepository = categoryRepository;
      }

    public Long createCategory(CategoryDTO categoryDTO)
      {
        Long communityId = categoryDTO.getCommunityId();
        Optional<Community> community = communityRepository.findById(communityId);

        Category category = Category.builder()
            .categoryId(categoryDTO.getCategoryId())
            .categoryName(categoryDTO.getCategoryName())
            .categoryType(categoryDTO.getCategoryType())
            .community(community.orElseThrow())
            .path(community.get().getCommunityName())
            .build();

        Long categoryId = categoryRepository.save(category).getCategoryId();
        return categoryId;
      }
  }
