package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Entity
@Setter
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

    @Builder.Default
    private Long vote = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
  }
