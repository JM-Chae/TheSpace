package com.thespace.thespace.dto;



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

    @NotNull(message = "제목을 입력해 주세요.")
    @Size(min = 1, max = 30, message = "제목 길이는 최소 1글자, 최대 30글자입니다.")
    private String title;

    @NotNull(message = "게시글을 입력해 주세요.")
    private String content;

    private String path;

    private String writer;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    private Long viewCount;

    private Long vote;

    private Long replyCount;

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long categoryId;
  }


