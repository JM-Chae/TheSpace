package com.thespace.common.notice;

import com.thespace.common.BaseEntity;
import com.thespace.common.fcm.FcmTopic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String body;

    private String url;

    private FcmTopic topic;

    public Notice() {}

    public Notice(String title, String body, String url, FcmTopic topic) {
        this.title = title;
        this.body = body;
        this.url = url;
        this.topic = topic;
    }
}
