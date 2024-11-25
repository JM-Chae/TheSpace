package com.thespace.thespace.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO
  {
    private Long categoryId;

    @NotNull
    @Size(min = 1, max = 30)
    private String categoryName;

    private String categoryType;

    private String path;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    @NotNull
    private Long communityId;
  }
