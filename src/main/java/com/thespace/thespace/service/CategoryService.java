package com.thespace.thespace.service;

import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.category.CategoryCreateDTO;
import com.thespace.thespace.dto.category.CategoryListDTO;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CommunityRepository communityRepository;
    private final UserService userService;
    private final UserRoleService userRoleService;

    public void create(CategoryCreateDTO categoryCreateDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleId("ADMIN_" + categoryCreateDTO.path()))) {
            return;
        }

        Long communityId = categoryCreateDTO.communityId();
        Community community = communityRepository.findById(communityId).orElseThrow();

        Category category = Category.builder()
            .categoryName(categoryCreateDTO.categoryName())
            .categoryType(categoryCreateDTO.categoryType())
            .community(community)
            .path(community.getCommunityName())
            .build();

        categoryRepository.save(category);
    }

    public List<CategoryListDTO> list(String path) {
        List<Category> result = categoryRepository.findByPath(path);

        return result.stream().map(category ->
                new CategoryListDTO(
                    category.getCategoryId(),
                    category.getCategoryName(),
                    category.getCategoryType(),
                    category.getCommunity().getCommunityId()
                )
            )
            .collect(Collectors.toList());
    }

    public void delete(Long categoryId, Authentication authentication, String communityName) {
        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleId("ADMIN_" + communityName))) {
            return;
        }

        categoryRepository.deleteById(categoryId);
    }
}
