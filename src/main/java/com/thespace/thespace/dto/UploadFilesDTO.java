package com.thespace.thespace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class UploadFilesDTO
  {
    private final List<MultipartFile> fileList;
  }
