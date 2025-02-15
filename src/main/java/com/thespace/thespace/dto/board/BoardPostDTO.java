package com.thespace.thespace.dto.board;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BoardPostDTO(@NotNull(message = "Title is a required field.")
                           @Size(min = 1, max = 30, message = "Title length is min 1 letter, max 30 letter.")
                           String title,
                           @NotNull(message = "Content is a required field.")
                           String content,
                           List<String> fileNames, // Since 'FileId' and 'FileName' are now handled separately, it seems more intuitive to divide the field
                           @NotNull(message = "Choose category.")
                           Long categoryId) {

}
