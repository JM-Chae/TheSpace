package com.thespace.thespace.service;

public interface UserRoleService
  {
    void register(String roleName);

    Long findRoleId(String roleName);

    String findRoleNameById(Long id);
  }
