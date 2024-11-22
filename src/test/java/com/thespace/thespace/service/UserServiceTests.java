package com.thespace.thespace.service;

import com.thespace.thespace.domain.User;
import com.thespace.thespace.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
class UserServiceTests
  {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleService userRoleService;

    @Test
    void checkUuid() throws Exception
      {

        User user = User.builder().id("testID2").password("testPassword").name("testName").email("test@test.com").uuid("testUUID").build();
        userRepository.save(user);

        String uuid = "testUUI";
        boolean check = userService.checkUuid(uuid);

        if (!check)
          {
            log.info("Not existing UUID");
            throw new Exception();
          }
        log.info("Existing UUID");
      }

    @Test
    @Transactional
    @Rollback(false)
    void TestSetRole()
      {
        String roleName = "community";
        userRoleService.register(roleName);
        User user = User.builder().id("testID").password("testPassword").name("testName").email("test@test.com").uuid("testUUID").build();
        userRepository.save(user);

        userService.setRole(user.getId(), "USER_COMMUNITY_ADMIN");
      }

    @Test
    public void findUserRoles()
      {
        String userId = "123456";

        log.info(userRepository.findRoleIdsByUserId(userId).toString());
      }
  }