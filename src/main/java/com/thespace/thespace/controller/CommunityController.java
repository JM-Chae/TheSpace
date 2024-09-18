package com.thespace.thespace.controller;

import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class CommunityController
  {
    private final CommunityService communityService;


    @GetMapping("/createCommunity")
    public void createCommunityGet()
      {
      }

    @PostMapping("/createCommunity")
    public String createCommunity(@Valid CommunityDTO communityDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
      {
        if(bindingResult.hasErrors())
          {
            Long CommunityId = communityService.createCommunity(communityDTO);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/createCommunity";
          }
        Long CommunityId = communityService.createCommunity(communityDTO);
        redirectAttributes.addFlashAttribute("result", "Community created successfully");
        return "redirect:/"+communityDTO.getCommunityName();
      }

  }
