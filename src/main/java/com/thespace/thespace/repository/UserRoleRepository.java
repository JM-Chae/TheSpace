package com.thespace.thespace.repository;

import com.thespace.thespace.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>
  {
    boolean existsByRole(String name);
    Optional<UserRole> findByRole(String role);
  }
