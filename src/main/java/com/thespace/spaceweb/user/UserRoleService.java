package com.thespace.spaceweb.user;


import com.thespace.common.exception.Exceptions.RoleNotFound;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleService
  {
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;

    public UserRoleService(UserRoleRepository userRoleRepository, @Lazy UserService userService) {
      this.userRoleRepository = userRoleRepository;
      this.userService = userService;
    }


    public Long findRoleIdByName(String roleName)
    {
      return userRoleRepository.findByRole(roleName).orElseThrow(RoleNotFound::new).getId();
    }

    public Long register(String roleName)
    {
      if (!userRoleRepository.existsByRole(roleName))
      {
        UserRole role = new UserRole();
        String roleSetName = "ADMIN_"+roleName.toUpperCase();
        boolean exist = userRoleRepository.existsByRole(roleSetName);
        if (!exist)
        {
          role.setRole(roleSetName);
          return userRoleRepository.save(role).getId();
        }
      }

      return findRoleIdByName(roleName);
    }
    public String findRoleNameById(Long id)
    {
      return userRoleRepository.findById(id).orElseThrow().getRole();
    }

    @Transactional
    public void setRole(String id, Long roleId) {
        UserRole userRole = userRoleRepository.findById(roleId).orElseThrow(RoleNotFound::new);
        User user = userService.findById(id);
        user.getRoles().add(userRole);
    }
  }