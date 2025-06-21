package com.thespace.spacechat.message;

import com.thespace.spacechat.message.MessageDTOs.Text;
import com.thespace.spaceweb.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat/send")
    public void send(Text dto, Authentication sender) {
        messageService.send(dto, (User) sender.getPrincipal());
    }

    @MessageMapping("/chat/recent")
    public void getRecent(Long rid, Authentication user) {
        messageService.getRecentMessages(rid, (User) user.getPrincipal());
    }
}
