package com.thespace.thespace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class HomeControllerTests
  {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHome() throws Exception
      {
        mockMvc.perform(get("/space"))
            .andExpect(status().isOk())
            .andExpect(content().string("Welcome to 'The Space'"))
            .andDo(MockMvcResultHandlers.print());
      }

  }