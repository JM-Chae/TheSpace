package com.thespace.thespace.service;

import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.CategoryDTO;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService
  {
    private CategoryRepository categoryRepository;
    private CommunityRepository communityRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setCommunityRepository(CommunityRepository communityRepository, ModelMapper modelMapper, CategoryRepository categoryRepository)
      {
        this.communityRepository = communityRepository;
        this.modelMapper = modelMapper;
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

    public List<CategoryDTO> getAllCategories(String path)
      {
        List<Category> result = categoryRepository.findByPath(path);
        List<CategoryDTO> categories = result.stream().map(category ->
            CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .communityId(category.getCommunity().getCommunityId())
                .build())
            .collect(Collectors.toList());

        return categories;
      }
  }
