package com.thespace.spaceweb.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService
  {
    private final UserRoleRepository userRoleRepository;

    public UserRole findByRoleName(String roleName) {
      return userRoleRepository.findByRole(roleName).orElseThrow();
    }

    public Long findRoleId(String roleName)
    {
      return userRoleRepository.findByRole(roleName).orElseThrow().getId();
    }

    public void register(String roleName)
    {
      if (!userRoleRepository.existsByRole(roleName))
      {
        UserRole role = new UserRole();
        String roleSetName = "ADMIN_"+roleName.toUpperCase();
        boolean exist = userRoleRepository.existsByRole(roleSetName);
        if (!exist)
        {
          role.setRole(roleSetName);
          userRoleRepository.save(role);
        }
      }
    }
    public String findRoleNameById(Long id)
    {
      return userRoleRepository.findById(id).orElseThrow().getRole();
    }
  }