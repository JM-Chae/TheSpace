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
public class Community extends BaseEntity
  {
    @Id
    private Long CommunityId;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String CommunityName;
  }
