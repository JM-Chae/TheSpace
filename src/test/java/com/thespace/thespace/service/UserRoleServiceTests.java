package com.thespace.thespace.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRoleServiceTests
  {
    @Autowired
    UserRoleService userRoleService;

    @Test
    void register()
      {
        String roleName = "community";
        userRoleService.register(roleName);
      }
  }