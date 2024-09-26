package com.thespace.thespace.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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

    @Column(nullable = false, length = 12)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    //implement user profile page, more detail intro, and share status setter later
  }
