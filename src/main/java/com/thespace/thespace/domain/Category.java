package com.thespace.thespace.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@ToString
@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity
  {
    @Id
    private int categoryId;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private String categoryType;
  }
