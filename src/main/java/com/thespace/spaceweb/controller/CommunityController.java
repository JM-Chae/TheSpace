package com.thespace.spaceweb.controller;

import com.thespace.spaceweb.dto.CommunityDTOs;
import com.thespace.spaceweb.dto.CommunityDTOs.Info;
import com.thespace.spaceweb.dto.page.PageReqDTO;
import com.thespace.spaceweb.dto.page.PageResDTO;
import com.thespace.spaceweb.service.CommunityService;
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
    public Info get(@PathVariable("communityName") String communityName) {
        return communityService.get(communityName);
    }

    @GetMapping("/list")
    public PageResDTO<Info> list(@ModelAttribute PageReqDTO pageReqDTO) {
        return communityService.list(pageReqDTO);
    }

    @PostMapping
    public Long create(@Valid @RequestBody CommunityDTOs.Create createDTO,
        Authentication authentication, @RequestParam("nameCheck") boolean nameCheck) {
        return communityService.create(createDTO, authentication, nameCheck);
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
    public void modify(@RequestBody CommunityDTOs.Modify modifyDTO,
        Authentication authentication) {
        communityService.modify(modifyDTO, authentication);
    }
}
