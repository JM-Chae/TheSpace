package com.thespace.common.fcm;

import com.thespace.spaceweb.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Getter
    private String token;

    private String sessionId;

    public FcmToken() {

    }

    public FcmToken(User user, String token, String sessionId) {
        this.user = user;
        this.token = token;
        this.sessionId = sessionId;
    }

    public void updateUserAndSessionId(User user, String sessionId) {
        this.user = user;
        this.sessionId = sessionId;
    }

}
