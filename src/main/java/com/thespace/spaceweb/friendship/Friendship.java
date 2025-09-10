package com.thespace.spaceweb.friendship;

import com.thespace.spaceweb.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User from;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User to;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String note;

    private LocalDateTime acceptedAt;

    public Friendship() {
    }

    public Friendship(User from, User to, Status status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public void acceptedAtWriter() {
        this.acceptedAt = LocalDateTime.now();
    }

    public void changeNote(String note) {
        this.note = note;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }
}
