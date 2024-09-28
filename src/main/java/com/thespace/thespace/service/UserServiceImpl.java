package com.thespace.thespace.service;

import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.UserDTO;
import com.thespace.thespace.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService
  {
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setRepository(UserRepository userRepository, ModelMapper modelMapper)
      {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
      }

    public void register(UserDTO userDTO, boolean check) throws Exception
      {
        if(!check)
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
  }
