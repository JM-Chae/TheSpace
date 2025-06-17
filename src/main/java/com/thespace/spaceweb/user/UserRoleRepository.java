package com.thespace.spaceweb.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>
  {
    boolean existsByRole(String name);
    Optional<UserRole> findByRole(String role);

  }
