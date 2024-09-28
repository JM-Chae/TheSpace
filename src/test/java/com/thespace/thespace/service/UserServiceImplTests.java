package com.thespace.thespace.service;

import com.thespace.thespace.domain.User;
import com.thespace.thespace.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class UserServiceImplTests
  {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void clean()
      {
        userRepository.deleteAll();
      }

    @Test
    void checkUuid() throws Exception
      {

        User user = User.builder().id("testID").password("testPassword").name("testName").email("test@test.com").uuid("testUUID").build();
        userRepository.save(user);

        String uuid = "testUUI";
        boolean check = userService.checkUuid(uuid);

        if (check)
          {
            log.info("Not existing UUID");
            throw new Exception();
          }
        log.info("Existing UUID");
      }
  }