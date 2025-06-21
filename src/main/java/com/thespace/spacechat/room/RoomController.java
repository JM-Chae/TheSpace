package com.thespace.spacechat.room;

import com.thespace.spacechat.room.RoomDTOs.Info;
import com.thespace.spacechat.room.RoomDTOs.Summary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Info> create(Authentication creator,@RequestBody RoomDTOs.Create dto) {

        Info res = roomService.create(creator, dto);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{rid}")
    public ResponseEntity<Info> getInfo(@PathVariable(name = "rid") Long rid) {

        Info res = roomService.get(rid);

        return ResponseEntity.ok(res);
    }

    @PutMapping("/update/{rid}")
    public ResponseEntity<Info> update(@PathVariable(name = "rid") Long rid, Authentication updater,@RequestBody RoomDTOs.Update dto) {
        Info res = roomService.update(rid, updater, dto);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/{rid}/members")
    public ResponseEntity<Info> invite(@PathVariable(name= "rid") Long rid, Authentication inviter, @RequestBody RoomDTOs.Invite dto) {
        Info res = roomService.invite(rid, inviter, dto);

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{rid}/members")
    public ResponseEntity<Info> kick(@PathVariable(name= "rid") Long rid, Authentication kicker, @RequestParam("targetUuid") String targetUuid) {
        Info res = roomService.kick(rid, kicker, targetUuid);

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{rid}/members/me")
    public void quit(@PathVariable(name = "rid") Long rid, Authentication quitter) {
        roomService.quit(rid, quitter);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Summary>> getMyRooms(Authentication me) {
        List<Summary> res = roomService.getMyRooms(me);

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{rid}")
    public void delete(@PathVariable(name = "rid") Long rid, Authentication deleter) {
        roomService.delete(rid, deleter);
    }

    @PutMapping("/{rid}/manager")
    public ResponseEntity<Info> delegate(@PathVariable(name = "rid") Long rid, Authentication manager, @RequestParam("targetUuid") String targetUuid) {
        Info res = roomService.delegate(rid, manager, targetUuid);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/{rid}/members/me")
    public ResponseEntity<Info> join(@PathVariable(name = "rid") Long rid, Authentication joiner) {
        Info res = roomService.join(rid, joiner);

        return ResponseEntity.ok(res);
    }
}
