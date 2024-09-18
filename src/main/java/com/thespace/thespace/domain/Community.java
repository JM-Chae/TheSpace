package com.thespace.thespace.domain;

import jakarta.persistence.*;
import lombok.*;

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

    private String communityName;

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> category  = new ArrayList<>();
  }
