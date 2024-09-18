package com.thespace.thespace.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO
  {
    private Long bno;

    @NotNull
    @Size(min = 1, max = 30)
    private String title;

    @NotNull
    private String content;

    private String path;

    @NotEmpty
    private String writer;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    private Long viewCount;

    private Long vote;

    private Long replyCount;

    @NotNull
    private Long categoryId;
  }


