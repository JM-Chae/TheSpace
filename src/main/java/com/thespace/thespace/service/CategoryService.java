package com.thespace.thespace.service;

import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.category.CategoryCreateDTO;
import com.thespace.thespace.dto.category.CategoryDTO;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CommunityRepository communityRepository;
    private final UserService userService;
    private final UserRoleService userRoleService;

    public Long create(CategoryCreateDTO categoryCreateDTO, String userId) {
        if (!userService.findUserRoles(userId)
            .contains(userRoleService.findRoleId("ADMIN_" + categoryCreateDTO.path()))) {
            return null;
        }

        Long communityId = categoryCreateDTO.communityId();
        Community community = communityRepository.findById(communityId).orElseThrow();

        Category category = Category.builder()
            .categoryName(categoryCreateDTO.categoryName())
            .categoryType(categoryCreateDTO.categoryType())
            .community(community)
            .path(community.getCommunityName())
            .build();

        return categoryRepository.save(category).getCategoryId();
    }

    public List<CategoryDTO> list(String path) {
        List<Category> result = categoryRepository.findByPath(path);

        return result.stream().map(category ->
                CategoryDTO.builder()
                    .categoryId(category.getCategoryId())
                    .categoryName(category.getCategoryName())
                    .communityId(category.getCommunity().getCommunityId())
                    .build())
            .collect(Collectors.toList());
    }

    public void deleteCategory(Long categoryId, String userId, String communityName) {
        if (userService.findUserRoles(userId)
            .contains(userRoleService.findRoleId("ADMIN_" + communityName))) {
            return;
        }

        categoryRepository.deleteById(categoryId);
    }
}
