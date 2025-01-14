package com.thespace.thespace.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@Builder
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;

    private String path;

    @Column(columnDefinition = "longtext")
    private String content;

    private String writer;

    private String writerUuid;

    @ManyToOne
    @JoinColumn(name = "Category_Id")
    private Category category;

    @Setter
    @ColumnDefault("0")
    private Long viewCount = 0L;

    @Setter
    @ColumnDefault("0")
    private Long vote = 0L;

    @Setter
    @ColumnDefault("0")
    private Long rCount = 0L;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private Set<BoardFile> fileSet = new HashSet<>();

    public Board() {
    }

    public Board(Long bno, String title, String path, String content, String writer,
        String writerUuid, Category category, Long viewCount, Long vote, Long rCount) {
        this.bno = bno;
        this.title = title;
        this.path = path;
        this.content = content;
        this.writer = writer;
        this.writerUuid = writerUuid;
        this.category = category;
        this.viewCount = viewCount;
        this.vote = vote;
        this.rCount = rCount;
    }

    public void change(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void addFile(String fileId, String fileName) {
        BoardFile boardFile = BoardFile.builder()
            .fileId(fileId)
            .fileName(fileName)
            .board(this)
            .ord(fileSet.size())
            .build();
        fileSet.add(boardFile);
    }
}
