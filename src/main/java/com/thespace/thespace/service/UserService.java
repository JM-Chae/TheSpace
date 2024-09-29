package com.thespace.thespace.service;

import com.thespace.thespace.dto.UserDTO;

public interface UserService
  {
    void register(UserDTO userDTO, boolean check) throws Exception;
    boolean checkUuid(String uuid);
    boolean checkId(String id);
    void setRole(String id, String role);
  }
