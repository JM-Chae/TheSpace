package com.thespace.thespace.dto.board;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BoardModifyDTO(Long bno,
                             @NotNull(message = "Title is a required field.")
                             @Size(min = 1, max = 30, message = "Title length is min 1 letter, max 30 letter.")
                             String title,
                             @NotNull(message = "Content is a required field.")
                             String content,
                             String writer,
                             String writerUuid,
                             @NotNull(message = "Choose category.")
                             Long categoryId,
                             List<String> fileNames) {

}
