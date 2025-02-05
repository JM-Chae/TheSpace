package com.thespace.thespace.controller;


import com.thespace.thespace.dto.user.UserInfoDTO;
import com.thespace.thespace.dto.user.UserRegisterDTO;
import com.thespace.thespace.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/checkid")
    public boolean check(@RequestParam("id") String id) {
        return userService.checkId(id);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userService.getUserinfoDTO(authentication));
    }

    @PostMapping
    public void register(@Valid @RequestBody UserRegisterDTO userRegisterDTO,
        @RequestParam("checkid") boolean check) {
        userService.register(userRegisterDTO, check);
    }
}
