package com.thespace.spacechat.domain;

import com.thespace.spaceweb.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @Size(min=4, max=20)
    private String name;
    private String managerUuid;
    @Size(min=4, max=200)
    private String description;
    @ManyToMany
    @JoinTable(name = "chat_room_members", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "uuid"))
    private Set<User> members;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ChatRoom() {
    }

    @Builder
    public ChatRoom(String name, String managerUuid, String description, Set<User> members, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.name = name;
        this.managerUuid = managerUuid;
        this.description = description;
        this.members = members;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Builder
    public void update(String name, String description, LocalDateTime modifiedAt) {
        this.name = name;
        this.description = description;
        this.modifiedAt = modifiedAt;
    }

    public void addMember(Set<User> user) {
        members.addAll(user);
    }
}
