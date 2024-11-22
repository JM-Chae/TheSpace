package com.thespace.thespace.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
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

    @Test
    public void findRoleId()
      {
        String roleName = "ROLE_USER";

        log.info(userRoleService.findRoleId(roleName).toString());
      }
  }