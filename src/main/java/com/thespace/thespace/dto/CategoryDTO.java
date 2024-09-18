package com.thespace.thespace.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO
  {
    private Long categoryId;

    @NotNull
    @Size(min = 1, max = 30)
    private String categoryName;

    @NotNull
    private String categoryType;

    private String path;

    private LocalDateTime categoryDate;

    private LocalDateTime modifiedDate;

    @NotNull
    private Long communityId;
  }
