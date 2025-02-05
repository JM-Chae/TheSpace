package com.thespace.thespace.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO(@NotNull(message = "Please enter your ID.") @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
                              String id,
                              @NotNull(message = "Please enter your name.") @Size(min = 2, max = 20, message = "Please enter between 2 and 20 characters.")
                              String name,
                              @NotNull @Email @Size(max = 50, message = "Incorrect email address. Please enter it again.")
                              String email,
                              @NotNull(message = "Please enter your password.") @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
                              String password) {

}
