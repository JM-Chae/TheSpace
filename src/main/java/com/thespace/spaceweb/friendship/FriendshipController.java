package com.thespace.spaceweb.friendship;

import com.thespace.common.page.PageReqDTO;
import com.thespace.common.page.PageResDTO;
import com.thespace.spaceweb.friendship.FriendshipDTOs.Friend;
import com.thespace.spaceweb.friendship.FriendshipDTOs.Info;
import com.thespace.spaceweb.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @DeleteMapping("/{fid}")
    public void deleteRequest(Authentication fromUser, @PathVariable("fid") Long fid) {

        friendshipService.deleteRequest((User) fromUser.getPrincipal(), fid);
    }

    @DeleteMapping("/{fid}/reject")
    public void rejectRequest(Authentication user, @PathVariable("fid") Long fid) {

        friendshipService.reject((User) user.getPrincipal(), fid);
    }

    @PostMapping("/{fid}/accept")
    public void accept(Authentication user, @PathVariable("fid") Long fid) {

        friendshipService.accept((User) user.getPrincipal(), fid);
    }

    @PostMapping("/block")
    public void block(Authentication fromUser, @RequestParam("toUserUuid") String toUserUuid) {

        friendshipService.block((User) fromUser.getPrincipal(), toUserUuid);
    }

    @PutMapping ("/{fid}/unblock")
    public void unblock(Authentication fromUser, @PathVariable("fid") Long fid) {

        friendshipService.unblock((User) fromUser.getPrincipal(), fid);
    }

    @PutMapping("/{fid}/note")
    public void note(Authentication fromUser, @PathVariable("fid") Long fid, @RequestParam("note") String note) {

        friendshipService.note((User) fromUser.getPrincipal(), fid, note);
    }

    @GetMapping
    public Info getFriendship(Authentication fromUser, @RequestParam("toUserUuid") String toUserUuid) {

        return friendshipService.getInfo((User) fromUser.getPrincipal(), toUserUuid);
    }

    @GetMapping("/list")
    public PageResDTO<Friend> getListFriends(Authentication user, @ModelAttribute PageReqDTO pageReqDTO) {

        return friendshipService.getFriendList((User) user.getPrincipal(), pageReqDTO);
    }
}
