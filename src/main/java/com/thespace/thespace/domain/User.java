package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails
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



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
      {
        return List.of();
      }

    @Override
    public String getUsername()
      {
        return this.id;
      }

    @Override
    public boolean isAccountNonExpired()
      {
        return UserDetails.super.isAccountNonExpired();
      }

    @Override
    public boolean isAccountNonLocked()
      {
        return UserDetails.super.isAccountNonLocked();
      }

    @Override
    public boolean isCredentialsNonExpired()
      {
        return UserDetails.super.isCredentialsNonExpired();
      }

    @Override
    public boolean isEnabled()
      {
        return UserDetails.super.isEnabled();
      }

    //implement user profile page, more detail intro, and share status setter later
  }
