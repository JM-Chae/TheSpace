package com.thespace.thespace.service;

import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.category.CategoryCreateDTO;
import com.thespace.thespace.dto.category.CategoryDTO;
import com.thespace.thespace.dto.community.CommunityCreateDTO;
import com.thespace.thespace.dto.community.CommunityDTO;
import com.thespace.thespace.dto.community.CommunityModifyDTO;
import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.repository.getList.GetListCommunity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final CategoryService categoryService;
    private final GetListCommunity getListCommunity;

    public Long create(CommunityCreateDTO communityCreateDTO, Authentication authentication, boolean nameCheck) {
        if (!nameCheck) {
            return null;
        }

        Community community = new Community(
            communityCreateDTO.communityName(),
            communityCreateDTO.description()
        );

        String communityName = communityCreateDTO.communityName();

        User user = (User) authentication.getPrincipal();

        userRoleService.register(communityName);
        userService.setRole(user.getId(), "ADMIN_" + communityName);

        Long communityId = communityRepository.save(community).getCommunityId();

        CategoryCreateDTO categoryCreateDTO = new CategoryCreateDTO("Open Forum", "Forum",
            community.getCommunityName(), communityId
        );
        categoryService.create(categoryCreateDTO, authentication);

        return communityId;
    }

    public boolean check(String communityName) {
        return !communityRepository.existsByCommunityName(communityName);
    }

    public PageResDTO<CommunityDTO> list(PageReqDTO pageReqDTO) {
        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.keyword();

        Pageable pageable = pageReqDTO.getPageable("communityId");

        Page<CommunityDTO> list = getListCommunity.getList(types, keyword, pageable);

        return PageResDTO.<CommunityDTO>PageResDTO()
            .pageReqDTO(pageReqDTO).dtoList(list.getContent()).total((int) list.getTotalElements())
            .build();
    }

    public void delete(Long communityId, Authentication authentication, String communityName) {
        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleId("ADMIN_" + communityName))) {
            return;
        }

        communityRepository.deleteById(communityId);
    }

    public List<Long> hasAdminList(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.findUserRoles(user.getId()).stream()
            .map(userRoleService::findRoleNameById)
            .filter(roleName -> roleName.contains("ADMIN"))
            .map(roleName -> getCommunityIdByName(roleName.split("_")[1]))
            .collect(Collectors.toList());
    }

    public CommunityDTO get(String communityName) {
        Community community = communityRepository.findByCommunityName(communityName);
        List<Category> categories = community.getCategory();
        List<CategoryDTO> categoryDTO = categories.stream().map(category ->
                new CategoryDTO(
                    category.getCategoryId(),
                    category.getCategoryName(),
                    category.getCategoryType(),
                    category.getPath(),
                    category.getCreateDate(),
                    category.getModDate(),
                    category.getCommunity().getCommunityId()
                ))
            .toList();

        return new CommunityDTO(
            community.getCommunityId(),
            community.getCommunityName(),
            community.getCreateDate(),
            community.getModDate(),
            community.getDescription(),
            categoryDTO
        );
    }

    public Long getCommunityIdByName(String communityName) {
        return communityRepository.findCommunityIdByNameIgnoreCase(communityName);
    }

    public void modify(CommunityModifyDTO communityModifyDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleId("ADMIN_" + communityModifyDTO.communityName()))) {
            return;
        }

        Community community = communityRepository.findById(communityModifyDTO.communityId())
            .orElseThrow();//add Exception

        community.change(communityModifyDTO.description());
        communityRepository.save(community);
    }
}
