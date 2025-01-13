package com.thespace.thespace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class UserInfoDTO
  {
    private final String uuid;
    private final String id;
    private final String name;
    private final String email;
    private final List<String> roles;
  }
