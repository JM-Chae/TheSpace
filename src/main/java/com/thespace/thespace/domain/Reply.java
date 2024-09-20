package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Reply extends BaseEntity
  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    private String path;

    private String replyContent;

    private String replyWriter;

    private Long vote;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
  }
