package com.thespace.spaceweb.board;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class BoardFile implements Comparable<BoardFile> {

    @Id
    private String fileId;
    @NotNull
    private String fileName;

    private String imageChk;
    @NotNull
    private int ord = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board = new Board();

    public BoardFile() {

    }

    @Builder
    public BoardFile(String fileId, String fileName, String imageChk, int ord, Board board) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.imageChk = imageChk;
        this.ord = ord;
        this.board = board;
    }

    public int compareTo(BoardFile o) {
        return this.ord - o.ord;
    }
}
