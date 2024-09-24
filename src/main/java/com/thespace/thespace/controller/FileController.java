package com.thespace.thespace.controller;


import com.thespace.thespace.dto.BoardFileDTO;
import com.thespace.thespace.dto.UploadFilesDTO;
import com.thespace.thespace.service.BoardFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController
  {
    @Value("${com.thespace.upload.path}")
    private String uploadPath;

    private BoardFileService boardFileService;

    @Autowired
    public FileController(BoardFileService boardFileService)
      {
        this.boardFileService = boardFileService;
      }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<BoardFileDTO> upload(UploadFilesDTO uploadFilesDTO)
      {
        if(uploadFilesDTO.getFileList() != null)
          {
            List<BoardFileDTO> list = new ArrayList<>();
            uploadFilesDTO.getFileList().forEach(multipartFile -> {
              String originalFileName = multipartFile.getOriginalFilename();
              String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
              Path savePath = Paths.get(uploadPath, uuid + "_" + originalFileName);

              boolean image = false;

              try
                {
                  multipartFile.transferTo(savePath);
                  if (Files.probeContentType(savePath).startsWith("image"))
                    {
                      image = true;
                      File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalFileName);
                      Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                }catch (IOException e)
                {
                  log.error("Exception while uploading file [Err_Msg]: {}", e.getMessage());
                }

              list.add(BoardFileDTO.builder()
                      .fileId(uuid)
                      .fileName(originalFileName)
                      .imageChk(image)
                  .build());
            });
            return list;
          }
        return null;
      }

    @GetMapping("/get/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename)
      {
        Resource resource = new FileSystemResource(uploadPath + File.separator + filename);
        HttpHeaders headers = new HttpHeaders();

        try
          {
          headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        }catch (Exception e)
          {
            return ResponseEntity.internalServerError().build();
          }
        return ResponseEntity.ok().headers(headers).body(resource);
      }

    @DeleteMapping("/delete/{filename}")
    public void deleteFile(@PathVariable("filename") String filename)
      {
        String fileId = filename.substring(0,8);
        Resource resource = new FileSystemResource(uploadPath + File.separator + filename);

        try
          {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            Files.delete(resource.getFile().toPath());
            boardFileService.deleteBoardFile(fileId);
            if (contentType.startsWith("image"))
              {
                File thumbnail = new File(uploadPath + File.separator +"s_"+ filename);
                Files.deleteIfExists(thumbnail.toPath());
              }
          }catch (Exception e)
          {
            log.error("Exception while deleting file [Err_Msg]: {}", e.getMessage());
          }
      }
  }
