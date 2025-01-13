package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;



@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Community extends BaseEntity
  // lombok을 jpa와 쓸 때 주의해야할 점
  // 1. NoArgsConstructor를 왜 붙이는가? 그리고 어떻게 붙여야하는가?
  // 1-1. JPA의 어떤 성질 때문일까?
  // 2. ToString()을 어떻게 사용해야할까? 왜 조심해야할까?
  // 3.
  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    @Column(unique = true)
    private String communityName;

    @Column(columnDefinition = "longtext")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<Category> category  = new ArrayList<>();

    public void change(String description)
      {
        this.description = description;
      }
  }