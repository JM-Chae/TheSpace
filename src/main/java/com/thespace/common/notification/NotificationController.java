package com.thespace.common.notification;

import com.thespace.common.notification.NotificationDTOs.DTO;
import com.thespace.common.page.PageReqDTO;
import com.thespace.common.page.PageResDTO;
import com.thespace.spaceweb.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public PageResDTO<DTO> getNotifications(Authentication user, /*@RequestParam(name = "isRead") boolean isRead,*/ @ModelAttribute PageReqDTO pageReqDTO) {

        return notificationService.getNotifications((User) user.getPrincipal(), /*isRead,*/ pageReqDTO);
    }

    @PatchMapping("/read")
    public void readNotifications(Authentication user, @RequestParam("nno") Long nno) {
        notificationService.changeToRead((User) user.getPrincipal(), nno);
    }
}
