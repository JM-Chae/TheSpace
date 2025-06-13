package com.thespace.spaceweb.repository;

import com.thespace.spaceweb.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface UserRepository extends JpaRepository<User, String>
  {
    boolean existsByUuid(String uuid);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.uuid = :uuid")
    Optional<User> findByUuid(@Param("uuid")String uuid);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") String id);

    @Query("SELECT r.id FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") String userId);

    @NonNull
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(@NonNull String id);
  }
