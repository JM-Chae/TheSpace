package com.thespace.thespace.controller;

import com.google.gson.Gson;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.UserDTO;
import com.thespace.thespace.repository.UserRepository;
import com.thespace.thespace.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserControllerTests
  {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;

    @BeforeEach
    public void clean()
      {
        userRepository.deleteAll();
      }

    @Test
    void testCheckId() throws Exception
      {
        User user = User.builder()
            .id("testID")
            .password("testPassword")
            .name("testName")
            .email("test@test.com")
            .uuid("testUUID")
            .build();
        userRepository.save(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/checkid")
                .param("id", "testID")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"))
            .andDo(MockMvcResultHandlers.print());
      }

    @Test
    void testRegister() throws Exception
      {
        UserDTO userDTO = UserDTO.builder()
            .id("testID")
            .password("testPassword")
            .name("testName")
            .email("test@test.com")
            .build();

        String content = gson.toJson(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .param("checkid", "true"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
      }
  }