
package com.thespace.common.fcm;

import com.thespace.common.notice.NoticeDTO;
import com.thespace.spaceweb.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
@Slf4j
public class FcmController {

    private final FcmService fcmService; 

    @PostMapping("/token")
    public ResponseEntity<Void> registerFcmToken(Authentication user, @RequestBody TokenRequest token, HttpServletRequest httpRequest) {
        log.info("Received FCM Token: {}", token);

        fcmService.saveToken((User) user.getPrincipal(), token.token(), httpRequest.getSession().getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteFcmToken(@RequestBody TokenRequest token) {
        log.info("Received FCM Token: {}", token);

        fcmService.deleteToken(token.token());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/send/notice")
    public ResponseEntity<Object> pushNotice(@RequestBody NoticeDTO dto) {
        fcmService.sendNotice(dto);

        return ResponseEntity.ok().build();
    }
}