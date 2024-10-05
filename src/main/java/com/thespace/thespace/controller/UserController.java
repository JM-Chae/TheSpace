package com.thespace.thespace.controller;


import com.thespace.thespace.dto.UserDTO;
import com.thespace.thespace.dto.UserInfoDTO;
import com.thespace.thespace.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController
  {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserController(UserService userService, PasswordEncoder passwordEncoder)
      {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
      }

    @PostMapping("/checkid")
    public boolean check(@RequestParam("id") String id, RedirectAttributes redirectAttributes)
      {
        boolean check = userService.checkId(id);
        if(!check)
          {
            redirectAttributes.addFlashAttribute("This ID already exists.");
          }
        redirectAttributes.addFlashAttribute("Successful Check ID");
        return check;
      }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(Authentication authentication)
      {
        if (authentication == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userService.getUserinfoDTO(authentication));
      }

    @PostMapping
    public void register(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, @RequestParam("checkid") boolean check, RedirectAttributes redirectAttributes) throws Exception
      {
        if(bindingResult.hasErrors())
          {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            throw new BindException(bindingResult);
          }
        String uuid;
        do
          {
            uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
          } while (!userService.checkUuid(uuid));

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userDTO.setUuid(uuid);
        userService.register(userDTO, check);
      }
  }
