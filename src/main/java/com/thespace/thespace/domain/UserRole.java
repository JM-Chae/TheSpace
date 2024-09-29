package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_role_data")
public class UserRole
  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String role;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();
  }
