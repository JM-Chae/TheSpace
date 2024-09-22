package com.thespace.thespace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BoardFile implements Comparable<BoardFile>
  {
    @Id
    private String fileId;
    private String fileName;
    private String imageChk;
    private int ord;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    public int compareTo(BoardFile o)
      {
        return this.ord - o.ord;
      }
  }
