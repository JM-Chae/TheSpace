package com.thespace.thespace.service;

import com.thespace.thespace.dto.UserDTO;
import com.thespace.thespace.dto.UserInfoDTO;
import org.springframework.security.core.Authentication;

public interface UserService
  {
    void register(UserDTO userDTO) throws Exception;
    boolean checkUuid(String uuid);
    boolean checkId(String id);
    void setRole(String id, String role);
    UserInfoDTO getUserinfoDTO(Authentication authentication);
  }
