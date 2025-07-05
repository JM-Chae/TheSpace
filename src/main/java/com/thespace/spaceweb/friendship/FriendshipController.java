package com.thespace.spaceweb.friendship;

import com.thespace.spaceweb.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping
    public void request(Authentication fromUser, @RequestParam("toUserUuid") String toUserUuid) {

        friendshipService.request((User) fromUser.getPrincipal(), toUserUuid);
    }

    @PostMapping("/{fid}")
    public void accept(Authentication user, @PathVariable("fid") Long fid) {

        friendshipService.accept((User) user.getPrincipal(), fid);
    }

    @PostMapping("/block")
    public void block(Authentication fromUser, @RequestParam("toUserUuid") String toUserUuid) {

        friendshipService.block((User) fromUser.getPrincipal(), toUserUuid);
    }

    @PutMapping("/{fid}/memo")
    public void memo(Authentication fromUser, @PathVariable("fid") Long fid, @RequestParam("memo") String memo) {

        friendshipService.memo((User) fromUser.getPrincipal(), fid, memo);
    }
}
