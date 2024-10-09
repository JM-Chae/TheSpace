package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


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

    private String replyWriterUuid;

    @Builder.Default
    @ColumnDefault("0")
    private Long vote = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
  }
