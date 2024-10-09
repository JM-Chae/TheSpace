package com.thespace.thespace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO
  {
    private Long rno;

    @NotNull
    private Long bno;

    @NotNull
    private String replyContent;

    private String replyWriter;

    private String replyWriterUuid;

    private LocalDateTime replyDate;

    private String path;

    private Long vote;
  }
