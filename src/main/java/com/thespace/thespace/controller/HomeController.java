package com.thespace.thespace.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController
  {
    @GetMapping("/space")
    public String home()
      {
        return "Welcome to 'The Space'";
      }
  }
