package com.thespace.thespace.service;


import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService
  {
    private final UserRoleRepository userRoleRepository;

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