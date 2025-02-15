package com.thespace.thespace.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Entity
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> board = new ArrayList<>();

    @Setter
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<UserRole> roles;

    public User() {
    }

    @Builder
    public User(String id, String uuid, String name, String email, String password,
        List<UserRole> roles) {
      this.id = id;
      this.uuid = uuid;
      this.name = name;
      this.email = email;
      this.password = password;
      this.roles = roles == null ? new ArrayList<>() : roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
