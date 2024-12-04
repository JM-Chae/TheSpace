package com.thespace.thespace.controller;

import com.thespace.thespace.dto.CategoryDTO;
import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.service.CategoryService;
import com.thespace.thespace.service.CommunityService;
import com.thespace.thespace.service.UserRoleService;
import com.thespace.thespace.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Log4j2
public class CommunityController
  {
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final CategoryService categoryService;
    private final CommunityService communityService;


    @GetMapping("/{communityname}")
    public CommunityDTO home(@PathVariable("communityname") String communityname)
      {
        return communityService.getCommunity(communityname);
      }


    @GetMapping("")
    public PageResDTO<CommunityDTO> getCommunityList(@RequestParam("page") int page, @RequestParam("keyword") String keyword, @RequestParam("type") String type)
      {
        {
          PageReqDTO pageReqDTO = PageReqDTO.builder()
              .page(page)
              .size(1000)
              .keyword(keyword)
              .type(type)
              .build();
          PageResDTO<CommunityDTO> getList = communityService.getCommunityList(pageReqDTO);

          return getList;
        }
      }

    @PostMapping("/create")
    public Long createCommunity(@RequestBody CommunityDTO communityDTO, @RequestParam("userid") String userid, @RequestParam("check") boolean check, RedirectAttributes redirectAttributes)
      {
        if (check)
          {
            String communityName = communityDTO.getCommunityName();
            if (communityName.isEmpty())
              {
                communityService.createCommunity(communityDTO);
                redirectAttributes.addFlashAttribute("errors", "communityName is empty");

                return null;
              }

            Long CommunityId = communityService.createCommunity(communityDTO);
            redirectAttributes.addFlashAttribute("result", "Community created successfully");

            userRoleService.register(communityName);
            userService.setRole(userid, "ADMIN_" + communityName);

            CategoryDTO categoryDTO = CategoryDTO.builder()
                .communityId(CommunityId)
                .categoryName("Open Forum")
                .categoryType("Forum")
                .build();

            categoryService.createCategory(categoryDTO);

            return CommunityId;
          }
        return null;
      }

    @GetMapping("/check")
    public boolean check(@RequestParam("communityName") String communityName)
      {
        return communityService.check(communityName);
      }

    @DeleteMapping
    public void deleteCommunity(@RequestBody CommunityDTO communityDTO, @RequestParam("userId") String userId, RedirectAttributes redirectAttributes)
      {
        if (userService.findUserRoles(userId).contains(userRoleService.findRoleId("ADMIN_" + communityDTO.getCommunityName())))
          {
            communityService.deleteCommunity(communityDTO.getCommunityId());
            redirectAttributes.addFlashAttribute("result", "Community deleted successfully");
          } else
          { // add Exception later
            redirectAttributes.addFlashAttribute("result", "You are not allowed to delete this community");
          }
      }

    @GetMapping("/hasAdmin")
    public List<Long> hasAdminCommunity(@RequestParam("userId") String userId)
      {
        List<Long> roles = userService.findUserRoles(userId);
        List<String> roleNames = roles.stream()
            .map(userRoleService::findRoleNameById)
            .toList();

        List<Long> findList = new ArrayList<>();
        roleNames.forEach(roleName -> {
          if (roleName.contains("ADMIN"))
            {
              findList.add(communityService.getCommunityIdByName(roleName.split("_")[1]));
            }
        });

        return findList;
      }
  }
