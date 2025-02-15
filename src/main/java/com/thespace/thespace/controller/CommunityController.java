package com.thespace.thespace.controller;

import com.thespace.thespace.dto.community.CommunityCreateDTO;
import com.thespace.thespace.dto.community.CommunityDTO;
import com.thespace.thespace.dto.community.CommunityModifyDTO;
import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.service.CommunityService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @GetMapping("/{communityName}")
    public CommunityDTO get(@PathVariable("communityName") String communityName) {
        return communityService.get(communityName);
    }

    @GetMapping("/list")
    public PageResDTO<CommunityDTO> list(@ModelAttribute PageReqDTO pageReqDTO) {
        return communityService.list(pageReqDTO);
    }

    @PostMapping
    public Long create(@Valid @RequestBody CommunityCreateDTO communityCreateDTO,
        Authentication authentication, @RequestParam("nameCheck") boolean nameCheck) {
        return communityService.create(communityCreateDTO, authentication, nameCheck);
    }

    @GetMapping("/nameCheck")
    public boolean check(@RequestParam("communityName") String communityName) {
        return communityService.check(communityName);
    }

    @DeleteMapping("/{communityId}")
    public void delete(@PathVariable("communityId") Long communityId,
        @RequestParam("communityName") String communityName,
        Authentication authentication) {
        communityService.delete(communityId, authentication, communityName);
    }

    @GetMapping("/list/admin")
    public List<Long> hasAdminList(Authentication authentication) {
        return communityService.hasAdminList(authentication);
    }

    @PatchMapping("/modify")
    public void modify(@RequestBody CommunityModifyDTO communityModifyDTO,
        Authentication authentication) {
        communityService.modify(communityModifyDTO, authentication);
    }
}
