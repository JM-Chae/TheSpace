package com.thespace.common.notification;

import com.thespace.common.BaseEntity;
import com.thespace.spaceweb.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nno;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(length = 50)
    private String title;

    private String body;

    private String url;

    @JdbcTypeCode(SqlTypes.JSON)
    private DataPayload dataPayload;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean is_read = false;

    public Notification() {}

    public Notification(User user, String title, String body, String url, DataPayload dataPayload, NotificationType type) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.url = url;
        this.dataPayload = dataPayload;
        this.type = type;
    }

    public void read(boolean is_read) {
        this.is_read = is_read;
    }

    public void updateDataPayload(DataPayload dataPayload) {
        this.dataPayload = dataPayload;
    }
}


