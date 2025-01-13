package com.thespace.thespace.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UploadFilesDTO
  {
    private final List<MultipartFile> fileList;
  }
