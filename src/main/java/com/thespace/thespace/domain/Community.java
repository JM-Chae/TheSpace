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
  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    @Column(unique = true)
    private String communityName;

    @Column(columnDefinition = "longtext")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> category  = new ArrayList<>();
  }
