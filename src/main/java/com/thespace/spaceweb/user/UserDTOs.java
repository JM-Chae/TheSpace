package com.thespace.spaceweb.user;

import com.thespace.spaceweb.user.UserDTOs.Info;
import com.thespace.spaceweb.user.UserDTOs.MyPage;
import com.thespace.spaceweb.user.UserDTOs.Register;
import com.thespace.spaceweb.user.UserDTOs.UpdateInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

sealed public interface UserDTOs permits Info, Register, MyPage, UpdateInfo {

    @Builder
    record Info(String uuid,
                String name,
                List<String> roles) implements UserDTOs {
    }

    record MyPage(String signature,
                  String name,
                  String uuid,
                  String introduce,
                  LocalDateTime joinedOn) implements UserDTOs {
    }

    record UpdateInfo(String signature, String name, String introduce) implements UserDTOs {

    }

    record Register(@NotNull(message = "Please enter your ID.") @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
                    String id,
                    @NotNull(message = "Please enter your name.") @Size(min = 2, max = 20, message = "Please enter between 2 and 20 characters.")
                    String name,
                    @NotNull @Email @Size(max = 50, message = "Incorrect email address. Please enter it again.")
                    String email,
                    @NotNull(message = "Please enter your password.") @Size(min = 6, max = 20, message = "Please enter between 6 and 20 characters.")
                    String password,
                    @Size(max = 200)
                    String introduce,
                    @NotNull
                    String signature) implements UserDTOs {

    }
}
