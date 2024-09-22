package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Board extends BaseEntity
  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;

    private String path;

    private String content;

    private String writer;

    @ManyToOne
    @JoinColumn(name = "Category_Id")
    private Category category;

    private Long viewCount;

    private Long vote;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardFile> fileSet = new HashSet<>();

    public void change(String title, String content)
      {
        this.title = title;
        this.content = content;
      }

    public void addFile(String fileId, String fileName)
      {
        BoardFile boardFile = BoardFile.builder()
            .fileId(fileId)
            .fileName(fileName)
            .board(this)
            .ord(fileSet.size())
            .build();
        fileSet.add(boardFile);
      }
  }
