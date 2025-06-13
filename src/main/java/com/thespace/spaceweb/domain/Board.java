package com.thespace.spaceweb.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @NotNull
    private String title;

    @NotNull
    private String path;

    @Column(columnDefinition = "longtext")
    @NotNull
    private String content;

    @ManyToOne
    @JoinColumn(name = "User_Id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Category_Id")
    private Category category;

    @Setter
    private Long viewCount = 0L;

    @Setter
    private Long vote = 0L;

    @Setter
    private Long rCount = 0L;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private Set<BoardFile> fileSet = new HashSet<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> reply = new ArrayList<>();

    public Board() {
    }

    @Builder
    public Board(String title, String path, String content, Category category, User user) {
        this.title = title;
        this.path = path;
        this.content = content;
        this.user = user;
        this.category = category;
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
