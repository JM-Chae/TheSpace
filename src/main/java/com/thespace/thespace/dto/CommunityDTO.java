package com.thespace.thespace.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class CommunityDTO
  {
    private Long communityId;

    @NotNull
    @Size(min = 1, max = 30)
    private String communityName;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private List<CategoryDTO> category;

  }
