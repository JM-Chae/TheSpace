package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(length = 30)
    private String title;

    private String path;

    private String content;

    private String writer;

    @ManyToOne
    @JoinColumn(name = "Category_Id")
    private Category category;

    private Long viewCount;

    private Long vote;

    //파일 첨부 나중에 구현


    public void change(String title, String content)
      {
        this.title = title;
        this.content = content;
      }
  }
