package com.thespace.thespace.controller;

import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.service.CommunityService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Log4j2
public class CommunityController
  {
    private final CommunityService communityService;


    @GetMapping()
    public void getCommunityList()
      {

      }

    @PostMapping("/create")
    public Long createCommunity(@Valid @RequestBody CommunityDTO communityDTO, BindingResult bindingResult, @RequestParam("check") boolean check, RedirectAttributes redirectAttributes)
      {
        if (check)
          {
            if (bindingResult.hasErrors())
              {
                communityService.createCommunity(communityDTO);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
              }

            Long CommunityId = communityService.createCommunity(communityDTO);
            redirectAttributes.addFlashAttribute("result", "Community created successfully");
            return CommunityId;
          }
        return 0L;
      }

    @GetMapping("/check")
    public boolean check(@RequestParam("communityName") String communityName)
      {
        return communityService.check(communityName);
      }
  }
