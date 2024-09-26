package com.thespace.thespace.repository;

import com.thespace.thespace.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>
  {
    boolean existsByUuid(String uuid);
  }
