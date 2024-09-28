package com.thespace.thespace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Please enter your ID.")
    @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
    private String id;

    @NotNull(message = "Please enter your name.")
    @Size(min = 2, max = 20, message = "Please enter between 2 and 20 characters.")
    private String name;

    @NotNull
    @Email
    @Size(max = 50, message = "Incorrect email address. Please enter it again.")
    private String email;

    @NotNull(message = "Please enter your password.")
    @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
    private String password;

    private LocalDateTime createDate;
  }
