package com.thespace.thespace.dto;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO
  {
    private Long bno;

    @NotNull(message = "Title is a required field.")
    @Size(min = 1, max = 30, message = "Title length is min 1 letter, max 30 letter.")
    private String title;

    @NotNull(message = "Content is a required field.")
    private String content;

    private String path;

    private String writer;

    private String writerUuid;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    private Long viewCount;

    private Long vote;

    private Long rCount;

    private List<String> fileNames;

    @NotNull(message = "Choose category.")
    private String categoryName;
  }


