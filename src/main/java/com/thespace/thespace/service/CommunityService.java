package com.thespace.thespace.service;

import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.category.CategoryCreateDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final CategoryService categoryService;
    private final GetListCommunity getListCommunity;

    public Long create(CommunityCreateDTO communityCreateDTO, String userid, boolean nameCheck) {
        if (!nameCheck) {
            return null;
        }

        Community community = modelMapper.map(communityCreateDTO, Community.class);

        String communityName = communityCreateDTO.communityName();

        userRoleService.register(communityName);
        userService.setRole(userid, "ADMIN_" + communityName);

        Long communityId = communityRepository.save(community).getCommunityId();

        CategoryCreateDTO categoryCreateDTO = new CategoryCreateDTO("Open Forum", "Forum",
            community.getCommunityName(), communityId);
        categoryService.create(categoryCreateDTO, userid);

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

    public void delete(Long communityId, String userId, String communityName) {
        if (!userService.findUserRoles(userId)
            .contains(userRoleService.findRoleId("ADMIN_" + communityName))) {
            return;
        }

        communityRepository.deleteById(communityId);
    }

    public List<Long> hasAdminList(String userId) {
        return userService.findUserRoles(userId).stream()
            .map(userRoleService::findRoleNameById)
            .filter(roleName -> roleName.contains("ADMIN"))
            .map(roleName -> getCommunityIdByName(roleName.split("_")[1]))
            .collect(Collectors.toList());
    }

    public CommunityDTO getCommunity(String communityName) {
        Community community = communityRepository.findByCommunityName(communityName);
        return modelMapper.map(community, CommunityDTO.class);
    }

    public Long getCommunityIdByName(String communityName) {
        return communityRepository.findCommunityIdByNameIgnoreCase(communityName);
    }

    public void modify(CommunityModifyDTO communityModifyDTO, String userId) {
        if (!userService.findUserRoles(userId)
            .contains(userRoleService.findRoleId("ADMIN_" + communityModifyDTO.communityName()))) {
            return;
        }

        Community community = communityRepository.findById(communityModifyDTO.communityId())
            .orElseThrow();//add Exception

        community.change(communityModifyDTO.description());
        communityRepository.save(community);
    }
}
