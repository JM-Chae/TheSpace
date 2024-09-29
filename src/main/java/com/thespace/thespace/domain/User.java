package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity
  {
    @Id
    @Column(nullable = false, length = 20)
    private String id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private List<UserRole> roles = new ArrayList<>();

    //implement user profile page, more detail intro, and share status setter later
  }
