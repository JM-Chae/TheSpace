package com.thespace.thespace.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity
  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String path;

    private String categoryName;

    private String categoryType;

    @ManyToOne
    @JoinColumn(name = "Community_Id")
    private Community community;

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> board  = new ArrayList<>();
  }
