package com.thespace.thespace.dto.board;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record BoardDTO(
    Long bno,
    String title,
    String content,
    String path,
    String writer,
    String writerUuid,
    LocalDateTime createDate,
    LocalDateTime modDate,
    Long viewCount,
    Long vote,
    Long rCount,
    List<String> fileNames,
    Long categoryId) {

}


