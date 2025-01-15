package com.thespace.thespace.controller;

import com.thespace.thespace.dto.community.CommunityCreateDTO;
import com.thespace.thespace.dto.community.CommunityDTO;
import com.thespace.thespace.dto.community.CommunityModifyDTO;
import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.service.CommunityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @GetMapping("/{communityname}")
    public CommunityDTO get(@PathVariable("communityname") String communityname) {
        return communityService.getCommunity(communityname);
    }

    @GetMapping("/list")
    public PageResDTO<CommunityDTO> list(@ModelAttribute PageReqDTO pageReqDTO) {
        return communityService.list(pageReqDTO);
    }

    @PostMapping("/admin")
    public Long create(@RequestBody CommunityCreateDTO communityCreateDTO,
        @RequestParam("userid") String userid, @RequestParam("nameCheck") boolean nameCheck) {
        return communityService.create(communityCreateDTO, userid, nameCheck);
    }

    @GetMapping("/nameCheck")
    public boolean check(@RequestParam("communityName") String communityName) {
        return communityService.check(communityName);
    }

    @DeleteMapping("/{communityId}")
    public void delete(@PathVariable("communityId") Long communityId,
        @RequestParam("communityName") String communityName,
        @RequestParam("userId") String userId) {
        communityService.delete(communityId, userId, communityName);
    }

    @GetMapping("/list/admin")
    public List<Long> hasAdminList(@RequestParam("userId") String userId) {
        return communityService.hasAdminList(userId);
    }

    @PutMapping("/modify")
    public void modify(@RequestBody CommunityModifyDTO communityModifyDTO,
        @RequestParam("userId") String userId) {
        communityService.modify(communityModifyDTO, userId);
    }
}
