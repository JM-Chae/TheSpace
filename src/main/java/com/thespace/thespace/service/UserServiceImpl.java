package com.thespace.thespace.service;

import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.UserDTO;
import com.thespace.thespace.exception.UserNotFound;
import com.thespace.thespace.repository.UserRepository;
import com.thespace.thespace.repository.UserRoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService
  {
    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setRepository(UserRepository userRepository, UserRoleRepository userRoleRepository, ModelMapper modelMapper)
      {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userRoleRepository = userRoleRepository;
      }

    public void register(UserDTO userDTO, boolean check) throws Exception
      {
        if (!check)
          {
            throw new Exception(); // Implement duplicateId Exception later
          }
        User user = modelMapper.map(userDTO, User.class);
        userRepository.save(user);
      }

    public boolean checkUuid(String uuid)
      {
        return !userRepository.existsByUuid(uuid);
      }

    public boolean checkId(String id)
      {
        return !userRepository.existsById(id);
      }

    public void setRole(String id, String role)
      {
        UserRole userRole = userRoleRepository.findById
            (userRoleRepository.findByRole(role).orElseThrow().getId()).orElseThrow(UserNotFound::new);
        User user = userRepository.findById(id).orElseThrow(UserNotFound::new);
        user.getRoles().add(userRole);
        userRepository.save(user);
      }
  } //Implement modify User data later
