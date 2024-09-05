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

    @Column(length = 30, nullable = false)
    private String title;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String writer;

    private Long viewCount;

    private Long vote;

    //파일 첨부 나중에 구현
  }
