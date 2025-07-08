package com.thespace.spaceweb.user;


import com.thespace.spaceweb.user.UserDTOs.Info;
import com.thespace.spaceweb.user.UserDTOs.MyPage;
import com.thespace.spaceweb.user.UserDTOs.UpdateInfo;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of(
            "headerName", token.getHeaderName(),
            "parameterName", token.getParameterName(),
            "token", token.getToken()
        );
    }


    @PostMapping("/login/me")
    public ResponseEntity<Info> rememberMe(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            userDetails.getPassword(),
            userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok(userService.getUserinfoDTO(auth));
    }

    @GetMapping("/checkid")
    public boolean check(@RequestParam("id") String id) {
        return userService.checkId(id);
    }

    @GetMapping("/info")
    public ResponseEntity<Info> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userService.getUserinfoDTO(authentication));
    }

    @PostMapping
    public void register(@Valid @RequestBody UserDTOs.Register userRegisterDTO,
        @RequestParam("checkid") boolean check) {
        userService.register(userRegisterDTO, check);
    }

    @GetMapping("/{uuid}/mypage")
    public ResponseEntity<MyPage> myPage(@PathVariable("uuid") String uuid) {

        return ResponseEntity.ok(userService.getMyPage(uuid));
    }

    @PatchMapping("/myinfo")
    public void updateMyInfo(Authentication user, @RequestBody UpdateInfo dto) {

        userService.updateInfo(dto, (User) user.getPrincipal());
    }
}
