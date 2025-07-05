package com.thespace.spaceweb.reply;

import com.thespace.common.BaseEntity;
import com.thespace.spaceweb.board.Board;
import com.thespace.spaceweb.user.User;
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
    private String replyContent;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String tag;

    @Setter
    private Long childCount = 0L;

    @Setter
    private Long vote = 0L;

    private Long parentRno = 0L;

    @Setter
    private Long taggedCount = 0L;

    private Long tagRno = 0L;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    public Reply() {

    }

    @Builder
    public Reply(Long tagRno, Long parentRno, String replyContent, User user,
        String tag, Board board) {
        this.tagRno = tagRno;
        this.parentRno = parentRno;
        this.replyContent = replyContent;
        this.user = user;
        this.tag = tag;
        this.board = board;
    }
}
