package com.thespace.spaceweb.community;

import com.thespace.common.page.PageReqDTO;
import com.thespace.common.page.PageResDTO;
import com.thespace.spaceweb.community.CommunityDTOs.Info;
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

    @GetMapping("/{communityId}")
    public Info get(@PathVariable("communityId") Long communityId) {
        return communityService.get(communityId);
    }

    @GetMapping("/list")
    public PageResDTO<Info> list(@ModelAttribute PageReqDTO pageReqDTO) {
        return communityService.list(pageReqDTO);
    }

    @PostMapping
    public Info create(@Valid @RequestBody CommunityDTOs.Create createDTO,
        Authentication authentication, @RequestParam("nameCheck") boolean nameCheck) {
        return communityService.create(createDTO, authentication, nameCheck);
    }

    @GetMapping("/nameCheck")
    public boolean check(@RequestParam("communityName") String communityName) {
        return communityService.check(communityName);
    }

    @DeleteMapping("/{communityId}")
    public void delete(@PathVariable("communityId") Long communityId,
        Authentication authentication) {
        communityService.delete(communityId, authentication);
    }

    @GetMapping("/list/admin")
    public List<Long> hasAdminList(Authentication authentication) {
        return communityService.hasAdminList(authentication);
    }

    @PatchMapping("/{communityId}/modify")
    public void modify(@PathVariable(name = "communityId")Long communityId, @RequestBody CommunityDTOs.Modify modifyDTO,
        Authentication authentication) {
        communityService.modify(communityId, modifyDTO, authentication);
    }
}
