package com.thespace.spaceweb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Entity
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @NotNull
    private String path;

    @NotNull
    private String replyContent;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String tag;

    @Setter
    private Long vote = 0L;

    @Setter
    private Long isNested = 0L;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    public Reply() {

    }

    @Builder
    public Reply(String path, String replyContent, User user,
        String tag, Board board) {
        this.path = path;
        this.replyContent = replyContent;
        this.user = user;
        this.tag = tag;
        this.board = board;
    }
}
