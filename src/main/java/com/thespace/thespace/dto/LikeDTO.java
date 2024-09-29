package com.thespace.thespace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDTO
  {
    private String userId;

    @Builder.Default
    private Long bno = 0L;

    @Builder.Default
    private Long rno = 0L;
  }
