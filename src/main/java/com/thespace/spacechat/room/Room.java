package com.thespace.spacechat.room;

import com.thespace.spaceweb.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;

@Getter
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @Size(min=4, max=20)
    private String name;
    @ManyToOne
    private User manager;
    @Size(min=4, max=200)
    private String description;
    @ManyToMany
    @JoinTable(name = "room_members", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "uuid"))
    private Set<User> members;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Room() {
    }

    public Room(String name, User manager, String description, Set<User> members, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.name = name;
        this.manager = manager;
        this.description = description;
        this.members = members;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void update(String name, String description, LocalDateTime modifiedAt) {
        this.name = name;
        this.description = description;
        this.modifiedAt = modifiedAt;
    }

    public void addMember(Set<User> user) {
        members.addAll(user);
    }

    public void delegate(User target) {
        this.manager = target;
    }
}
