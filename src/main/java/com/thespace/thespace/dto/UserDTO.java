package com.thespace.thespace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO
  {
    private String uuid;

    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private LocalDateTime createDate;
  }
