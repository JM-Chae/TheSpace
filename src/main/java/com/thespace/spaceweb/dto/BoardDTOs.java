package com.thespace.spaceweb.dto;

import com.thespace.spaceweb.dto.BoardDTOs.FileInfo;
import com.thespace.spaceweb.dto.BoardDTOs.Info;
import com.thespace.spaceweb.dto.BoardDTOs.Modify;
import com.thespace.spaceweb.dto.BoardDTOs.Post;
import com.thespace.spaceweb.dto.BoardDTOs.UploadFiles;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

sealed public interface BoardDTOs permits Info, FileInfo, Modify, Post,
    UploadFiles {

    @Builder
    record Info(
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
        Long categoryId) implements BoardDTOs {

    }

    @Builder
    record FileInfo(String fileId,
                    String fileName,
                    boolean imageChk,
                    int ord) implements BoardDTOs {

    }

    record Modify(Long bno,
                  @NotNull(message = "Title is a required field.")
                                 @Size(min = 1, max = 30, message = "Title length is min 1 letter, max 30 letter.")
                                 String title,
                  @NotNull(message = "Content is a required field.")
                                 String content,
                  @NotNull
                                 String writer,
                  @NotNull(message = "Choose category.")
                                 Long categoryId,
                  List<String> fileNames) implements BoardDTOs {

    }

    record Post(@NotNull(message = "Title is a required field.")
                               @Size(min = 1, max = 30, message = "Title length is min 1 letter, max 30 letter.")
                               String title,
                @NotNull(message = "Content is a required field.")
                               String content,
                List<String> fileNames,// Since 'FileId' and 'FileName' are now handled separately, it seems more intuitive to divide the field
                @NotNull(message = "Choose category.")
                               Long categoryId) implements BoardDTOs {

    }

    record UploadFiles(List<MultipartFile> fileList) implements BoardDTOs {

    }
}
