package com.thespace.spacechat.controller;

import com.thespace.spacechat.dto.ChatRoomDTOs;
import com.thespace.spacechat.dto.ChatRoomDTOs.Info;
import com.thespace.spacechat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/room")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<Info> create(Authentication creator,@RequestBody ChatRoomDTOs.Create dto) {

        Info res = chatRoomService.create(creator, dto);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{rid}")
    public ResponseEntity<Info> get(@PathVariable(name = "rid") Long rid) {

        Info res = chatRoomService.get(rid);

        return ResponseEntity.ok(res);
    }

    @PutMapping("/update/{rid}")
    public ResponseEntity<Info> update(@PathVariable(name = "rid") Long rid, Authentication updater,@RequestBody ChatRoomDTOs.Update dto) {
        Info res = chatRoomService.update(rid, updater, dto);

        return ResponseEntity.ok(res);
    }

    @PutMapping("/invite/{rid}")
    public ResponseEntity<Info> invite(@PathVariable(name= "rid") Long rid, Authentication inviter, @RequestBody ChatRoomDTOs.Invite dto) {
        Info res = chatRoomService.invite(rid, inviter, dto);

        return ResponseEntity.ok(res);
    }

    @PutMapping("/kick/{rid}")
    public ResponseEntity<Info> kick(@PathVariable(name= "rid") Long rid, Authentication kicker, String targetUuid) {
        Info res = chatRoomService.kick(rid, kicker, targetUuid);

        return ResponseEntity.ok(res);
    }

    @PutMapping("/quit/{rid}")
    public void quit(@PathVariable(name = "rid") Long rid, Authentication quitter) {
        chatRoomService.quit(rid, quitter);
    }
}
