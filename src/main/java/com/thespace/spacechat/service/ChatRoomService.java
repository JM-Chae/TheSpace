package com.thespace.spacechat.service;

import com.thespace.spacechat.domain.ChatRoom;
import com.thespace.spacechat.dto.ChatRoomDTOs;
import com.thespace.spacechat.dto.ChatRoomDTOs.Info;
import com.thespace.spacechat.exception.NotMember;
import com.thespace.spacechat.exception.NotRoomManager;
import com.thespace.spacechat.exception.RoomNotFound;
import com.thespace.spacechat.repository.RoomRepository;
import com.thespace.spaceweb.domain.User;
import com.thespace.spaceweb.domain.UserRole;
import com.thespace.spaceweb.dto.UserDTOs;
import com.thespace.spaceweb.service.UserService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final RoomRepository roomRepository;
    private final UserService userService;

    ChatRoom findRoom(Long rid) {
        return roomRepository.findById(rid).orElseThrow(RoomNotFound::new);
    }

    Set<User> membersListToSet(List<String> dto) {
        return userService.getUsersByUuid(dto);
    }

    Info entityToDTO(ChatRoom entity) {
        List<UserDTOs.Info> members = new ArrayList<>(entity.getMembers()
            .stream()
            .map(member -> new UserDTOs.Info(
                member.getUuid(),
                member.getName(),
                member.getRoles().stream().map(UserRole::getRole)
                    .toList()
            )).toList());

        return new Info(entity.getRoomId(),
            entity.getName(),
            entity.getManagerUuid(),
            entity.getDescription(),
            members,
            entity.getCreatedAt(),
            entity.getModifiedAt());
    }

    public Info create(Authentication creator,ChatRoomDTOs.Create dto) {

        Set<User> members = membersListToSet(dto.members());

        User creatorInfo = (User)creator.getPrincipal();

        ChatRoom res = roomRepository.save(new ChatRoom(
            dto.name(),
            creatorInfo.getUuid(),
            dto.description(),
            members,
            LocalDateTime.now(),
            LocalDateTime.now()));

        return entityToDTO(res);
    }

    public Info get(Long rid) {
        return entityToDTO(findRoom(rid));
    }

    @Transactional
    public Info update(Long rid, Authentication updater, ChatRoomDTOs.Update dto) {
        ChatRoom room = findRoom(rid);

        User updaterInfo = (User)updater.getPrincipal();

        if (Objects.equals(room.getManagerUuid(), updaterInfo.getUuid())) {
            room.update(dto.name(), dto.description(), LocalDateTime.now());
            return get(rid);
        }

        return null;
    }

    @Transactional
    public Info invite(Long rid, Authentication inviter, ChatRoomDTOs.Invite dto) {

        ChatRoom room = findRoom(rid);

        User inviterInfo = (User)inviter.getPrincipal();
        Set<User> targets = membersListToSet(dto.members());

        if (room.getMembers().contains(inviterInfo)) {
            room.addMember(targets);

            return entityToDTO(room);
        }

        //Push notice "inviter invited members to room."

        return null;
    }

    @Transactional
    public Info kick(Long rid, Authentication kicker, String targetUuid) {
        ChatRoom room = findRoom(rid);

        User kickerInfo = (User)kicker.getPrincipal();
        User target = userService.findUserByUuid(targetUuid);

        if (!Objects.equals(room.getManagerUuid(), kickerInfo.getUuid())) {
            throw new NotRoomManager();
        }

        if (!room.getMembers().contains(kickerInfo)) {
            throw new NotMember();
        }

        room.getMembers().remove(target);

        return entityToDTO(room);
    }

    @Transactional
    public void quit(Long rid, Authentication quitter) {
        ChatRoom room = findRoom(rid);
        User quitterInfo = (User)quitter.getPrincipal();

        if(!room.getMembers().contains(quitterInfo)) {
            throw new NotMember();
        }

        room.getMembers().remove(quitterInfo);
    }
}
