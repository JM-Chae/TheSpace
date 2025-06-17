package com.thespace.spacechat.room;

import com.thespace.common.exception.NotMember;
import com.thespace.common.exception.NotRoomManager;
import com.thespace.common.exception.RoomNotFound;
import com.thespace.spacechat.room.ChatRoomDTOs.Info;
import com.thespace.spacechat.room.ChatRoomDTOs.Summary;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserDTOs;
import com.thespace.spaceweb.user.UserRole;
import com.thespace.spaceweb.user.UserService;
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
                member.getRoles().stream().map(UserRole::getRole).toList()
            )).toList());

        User managerE = entity.getManager();
        UserDTOs.Info manager = new UserDTOs.Info(
            managerE.getUuid(),
            managerE.getName(),
            managerE.getRoles()
                .stream()
                .map(UserRole::getRole)
                .toList()
        );

        return new Info(
            entity.getRoomId(),
            entity.getName(),
            manager,
            entity.getDescription(),
            members,
            entity.getCreatedAt(),
            entity.getModifiedAt()
        );
    }

    public Info create(Authentication creator, ChatRoomDTOs.Create dto) {

        Set<User> members = membersListToSet(dto.members());
        User creatorInfo = (User) creator.getPrincipal();
        members.add(creatorInfo);

        ChatRoom res = roomRepository.save(new ChatRoom(
            dto.name(),
            creatorInfo,
            dto.description(),
            members,
            LocalDateTime.now(),
            LocalDateTime.now()
        ));

        return entityToDTO(res);
    }

    public Info get(Long rid) {
        return entityToDTO(findRoom(rid));
    }

    @Transactional
    public Info update(Long rid, Authentication updater, ChatRoomDTOs.Update dto) {
        ChatRoom room = findRoom(rid);

        User updaterInfo = (User) updater.getPrincipal();

        if (!Objects.equals(room.getManager().getUuid(), updaterInfo.getUuid())) {
            throw new NotRoomManager();
        }

        room.update(dto.name(), dto.description(), LocalDateTime.now());
        return get(rid);
    }

    @Transactional
    public Info invite(Long rid, Authentication inviter, ChatRoomDTOs.Invite dto) {

        ChatRoom room = findRoom(rid);

        User inviterInfo = (User) inviter.getPrincipal();
        Set<User> targets = membersListToSet(dto.members());

        if (!room.getMembers().contains(inviterInfo)) {
            throw new NotMember();
        }

        //Push notice "inviter invited members to room."

        room.addMember(targets);

        return entityToDTO(room);
    }

    @Transactional
    public Info kick(Long rid, Authentication kicker, String targetUuid) {
        ChatRoom room = findRoom(rid);

        User kickerInfo = (User) kicker.getPrincipal();
        User target = userService.findUserByUuid(targetUuid);

        if (!Objects.equals(room.getManager().getUuid(), kickerInfo.getUuid())) {
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
        User quitterInfo = (User) quitter.getPrincipal();

        if (!room.getMembers().contains(quitterInfo)) {
            throw new NotMember();
        }

        room.getMembers().remove(quitterInfo);
    }

    @Transactional
    public List<Summary> getMyRooms(Authentication me) {
        User myInfo = (User) me.getPrincipal();

        List<ChatRoom> rooms = userService.findRoomIds(myInfo.getUuid())
            .stream()
            .map(this::findRoom).toList();
        //Currently, Fail with an exception if even one room does not exist.

        return rooms
            .stream()
            .map(r -> new Summary(
                r.getRoomId(),
                r.getName(),
                "Add later",
                LocalDateTime.now()
            ))
            .toList();
    }

    @Transactional
    public void delete(Long rid, Authentication deleter) {
        ChatRoom room = findRoom(rid);
        User deleterInfo = (User) deleter.getPrincipal();

        if (!Objects.equals(room.getManager().getUuid(), deleterInfo.getUuid())) {
            throw new NotRoomManager();
        }

        roomRepository.deleteById(rid);
    }

    @Transactional
    public Info delegate(Long rid, Authentication manager, String targetUuid) {
        ChatRoom room = findRoom(rid);
        User managerInfo = (User) manager.getPrincipal();
        User targetUser = userService.findUserByUuid(targetUuid);

        if (!Objects.equals(room.getManager().getUuid(), managerInfo.getUuid())) {
            throw new NotRoomManager();
        }

        if (!room.getMembers().contains(targetUser)) {
            throw new NotMember();
        }

        room.delegate(targetUser);

        return entityToDTO(room);
    }
}
