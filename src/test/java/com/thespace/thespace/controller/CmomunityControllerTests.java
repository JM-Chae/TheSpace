package com.thespace.thespace.controller;

import com.google.gson.Gson;
import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.service.CommunityService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
public class CmomunityControllerTests
  {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private Gson gson;

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
        CommunityDTO community = CommunityDTO.builder()
            .communityName("TestTest").build();

        String communityJson = gson.toJson(community);
        boolean check = true;

        mockMvc.perform(MockMvcRequestBuilders.post("/community/create")
            .content(communityJson).contentType(MediaType.APPLICATION_JSON)
            .param("check", String.valueOf(check)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());

      }
  }
