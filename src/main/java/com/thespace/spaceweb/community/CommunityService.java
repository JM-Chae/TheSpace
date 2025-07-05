package com.thespace.spaceweb.community;

import com.thespace.common.exception.AlreadyExists;
import com.thespace.common.exception.CommunityNotFound;
import com.thespace.common.getList.GetListCommunity;
import com.thespace.common.page.PageReqDTO;
import com.thespace.common.page.PageResDTO;
import com.thespace.spaceweb.category.CategoryDTOs;
import com.thespace.spaceweb.category.CategoryService;
import com.thespace.spaceweb.community.CommunityDTOs.Info;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserRoleService;
import com.thespace.spaceweb.user.UserService;
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

    Info entityToDto(Community community) {
        return new Info(community.getId(),
            community.getName(),
            community.getCreateDate(),
            community.getModDate(),
            community.getDescription());
    }

    public Info create(CommunityDTOs.Create createDTO, Authentication authentication, boolean nameCheck) {
        if (!nameCheck) {
            return null;
        }

        Community community = new Community(
            createDTO.communityName(),
            createDTO.description()
        );

        String communityName = createDTO.communityName();

        User user = (User) authentication.getPrincipal();

        Long roleId = userRoleService.register(communityName);
        userRoleService.setRole(user.getId(), roleId);

        Info res = entityToDto(communityRepository.save(community));

        CategoryDTOs.Create categoryCreateDTO = new CategoryDTOs.Create("Open Forum", "Forum", res.id());
        categoryService.create(categoryCreateDTO, authentication);

        return res;
    }

    public boolean check(String communityName) {
        boolean check = !communityRepository.existsByName(communityName);
        if (!check) throw new AlreadyExists();

        else return true;
    }

    public PageResDTO<CommunityDTOs.Info> list(PageReqDTO pageReqDTO) {
        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.keyword();

        Pageable pageable = pageReqDTO.getPageable("id");

        Page<CommunityDTOs.Info> list = getListCommunity.getList(types, keyword, pageable);

        return PageResDTO.<CommunityDTOs.Info>PageResDTO()
            .pageReqDTO(pageReqDTO).dtoList(list.getContent()).total((int) list.getTotalElements())
            .build();
    }

    public void delete(Long communityId, Authentication authentication, String communityName) {
        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleIdByName("ADMIN_" + communityName))) {
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

    public CommunityDTOs.Info get(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(CommunityNotFound::new);

        return new CommunityDTOs.Info(
            community.getId(),
            community.getName(),
            community.getCreateDate(),
            community.getModDate(),
            community.getDescription()
        );
    }

    public Long getCommunityIdByName(String communityName) {
        return communityRepository.findCommunityIdByNameIgnoreCase(communityName);
    }

    public void modify(Long communityId, CommunityDTOs.Modify modifyDTO, Authentication authentication) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(CommunityNotFound::new);

        User user = (User) authentication.getPrincipal();
        if (!userService.findUserRoles(user.getId())
            .contains(userRoleService.findRoleIdByName("ADMIN_" + community.getName()))) {
            return;
        }

        community.change(modifyDTO.description());
        communityRepository.save(community);
    }
}
