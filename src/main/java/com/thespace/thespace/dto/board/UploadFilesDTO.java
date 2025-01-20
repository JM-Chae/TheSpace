package com.thespace.thespace.dto.board;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record UploadFilesDTO(List<MultipartFile> fileList) {

}
