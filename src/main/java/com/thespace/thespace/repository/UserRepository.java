package com.thespace.thespace.repository;

import com.thespace.thespace.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>
  {
    boolean existsByUuid(String uuid);
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") String id);

    @Query("SELECT r.id FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") String userId);
  }
