package com.thespace.thespace.controller;


import com.thespace.thespace.dto.UserDTO;
import com.thespace.thespace.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @PostMapping
    public void register(@RequestBody UserDTO userDTO, @RequestParam("checkd") boolean check) throws Exception
      {
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
