package com.thespace.spaceweb.user;

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

    @Query("SELECT r.roomId FROM User u JOIN u.rooms r WHERE u.uuid = :uuid")
    List<Long> findRoomIdsByUuid(@Param("uuid") String uuid);

    @Query("SELECT r.id FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") String userId);

    @NonNull
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(@NonNull String id);

    @NonNull
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUuid(@NonNull String uuid);
  }
