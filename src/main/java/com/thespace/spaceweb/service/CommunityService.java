package com.thespace.spaceweb.service;

import com.thespace.spaceweb.domain.Category;
import com.thespace.spaceweb.domain.Community;
import com.thespace.spaceweb.domain.User;
import com.thespace.spaceweb.dto.CategoryDTOs;
import com.thespace.spaceweb.dto.CommunityDTOs;
import com.thespace.spaceweb.dto.page.PageReqDTO;
import com.thespace.spaceweb.dto.page.PageResDTO;
import com.thespace.spaceweb.exception.AlreadyExists;
import com.thespace.spaceweb.repository.CommunityRepository;
import com.thespace.spaceweb.repository.getList.GetListCommunity;
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

    public Long create(CommunityDTOs.Create createDTO, Authentication authentication, boolean nameCheck) {
        if (!nameCheck) {
            return null;
        }

        Community community = new Community(
            createDTO.communityName(),
            createDTO.description()
        );

        String communityName = createDTO.communityName();

        User user = (User) authentication.getPrincipal();

        userRoleService.register(communityName);
        userService.setRole(user.getId(), "ADMIN_" + communityName);

        Long communityId = communityRepository.save(community).getCommunityId();

        CategoryDTOs.Create categoryCreateDTO = new CategoryDTOs.Create("Open Forum", "Forum",
            community.getCommunityName(), communityId
        );
        categoryService.create(categoryCreateDTO, authentication);

        return communityId;
    }

    public boolean check(String communityName) {
        boolean check = !communityRepository.existsByCommunityName(communityName);
        if (!check) throw new AlreadyExists();

        else return true;
    }

    public PageResDTO<CommunityDTOs.Info> list(PageReqDTO pageReqDTO) {
        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.keyword();

        Pageable pageable = pageReqDTO.getPageable("communityId");

        Page<CommunityDTOs.Info> list = getListCommunity.getList(types, keyword, pageable);

        return PageResDTO.<CommunityDTOs.Info>PageResDTO()
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

    public CommunityDTOs.Info get(String communityName) {
        Community community = communityRepository.findByCommunityName(communityName);
        List<Category> categories = community.getCategory();
        List<CategoryDTOs.Info> categoryDTO = categories.stream().map(category ->
                new CategoryDTOs.Info(
                    category.getCategoryId(),
                    category.getCategoryName(),
                    category.getCategoryType(),
                    category.getPath(),
                    category.getCreateDate(),
                    category.getModDate(),
                    category.getCommunity().getCommunityId()
                ))
            .toList();

        return new CommunityDTOs.Info(
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

    public void modify(CommunityDTOs.Modify modifyDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleId("ADMIN_" + modifyDTO.communityName()))) {
            return;
        }

        Community community = communityRepository.findById(modifyDTO.communityId())
            .orElseThrow();//add Exception

        community.change(modifyDTO.description());
        communityRepository.save(community);
    }
}
