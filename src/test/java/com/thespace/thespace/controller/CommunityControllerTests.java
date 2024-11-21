package com.thespace.thespace.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
public class CommunityControllerTests
  {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void check() throws Exception
      {
        String communityName = "aaa";

        mockMvc.perform(MockMvcRequestBuilders.get("/community/check")
                .param("communityName", communityName))
            .andExpect(MockMvcResultMatchers.content().string("true"))
            .andDo(MockMvcResultHandlers.print());
      }

    @Test
    @Transactional
    public void createCommunity() throws Exception
      {


        String communityName = "testtest";
        boolean check = true;

        mockMvc.perform(MockMvcRequestBuilders.post("/community/create")
                .param("communityName", communityName)
                .param("check", String.valueOf(check)).param("userid", "123456"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
      }

    @Test
    public void getList() throws Exception
      {
        String type = "n";
        String keyword = "";
        int page = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/community")
                .param("page", String.valueOf(page)).param("type", type).param("keyword", keyword))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
      }
  }
