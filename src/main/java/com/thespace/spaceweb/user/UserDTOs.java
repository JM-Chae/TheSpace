package com.thespace.spaceweb.user;

import com.thespace.spaceweb.user.UserDTOs.Info;
import com.thespace.spaceweb.user.UserDTOs.Register;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

sealed public interface UserDTOs permits Info, Register {

    @Builder
    record Info(String uuid,
                String name,
                List<String> roles) implements UserDTOs {
    }

    record Register(@NotNull(message = "Please enter your ID.") @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
                    String id,
                    @NotNull(message = "Please enter your name.") @Size(min = 2, max = 20, message = "Please enter between 2 and 20 characters.")
                    String name,
                    @NotNull @Email @Size(max = 50, message = "Incorrect email address. Please enter it again.")
                    String email,
                    @NotNull(message = "Please enter your password.") @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
                    String password) implements UserDTOs {

    }
}
