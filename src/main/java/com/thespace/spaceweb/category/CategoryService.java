package com.thespace.spaceweb.category;

import com.thespace.common.exception.CommunityNotFound;
import com.thespace.spaceweb.community.Community;
import com.thespace.spaceweb.community.CommunityRepository;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserRoleService;
import com.thespace.spaceweb.user.UserService;
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

    public void create(CategoryDTOs.Create categoryCreateDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Community community = communityRepository.findById(categoryCreateDTO.communityId()).orElseThrow();

        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleIdByName("ADMIN_" + community.getName()))) {
            return;
        }

        Category category = Category.builder()
            .name(categoryCreateDTO.name())
            .type(categoryCreateDTO.type())
            .community(community)
            .build();

        categoryRepository.save(category);
    }

    public List<CategoryDTOs.List> list(Long communityId) {

        Community community = communityRepository.findById(communityId).orElseThrow(CommunityNotFound::new);

        List<Category> result = categoryRepository.findByCommunity(community);

        return result.stream().map(category ->
                new CategoryDTOs.List(
                    category.getId(),
                    category.getName(),
                    category.getType(),
                    category.getCommunity().getId()
                )
            )
            .collect(Collectors.toList());
    }

    public void delete(Long categoryId, Authentication authentication, String communityName) {
        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleIdByName("ADMIN_" + communityName))) {
            return;
        }

        categoryRepository.deleteById(categoryId);
    }
}
