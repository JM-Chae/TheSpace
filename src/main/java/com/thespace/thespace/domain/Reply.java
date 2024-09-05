package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;


@ToString
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

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String replyContent;

    @Column(nullable = false)
    private String replyWriter;

    private Long vote;
  }
