package com.thespace.thespace.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

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
