package com.thespace.thespace.controller;


import com.thespace.thespace.dto.BoardFileDTO;
import com.thespace.thespace.dto.UploadFilesDTO;
import com.thespace.thespace.service.BoardFileService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// REST API란 무엇인가?
// 자원을 어떻게 나타내는가? 어떻게 나타내는게 좋을까?

// URI vs URL
// URI : Uniform Resource Identifier
// URL : Uniform Resource Locator

// Http Method(GET, PUT, POST, PATCH, DELETE) 를 기반으로 자원을 표현해주자.

// GET domain/get/{fileid}/{filename}
// GET domain/files?name=파일명

// 1. 명사를 복수형으로 표현한다.
// ex) GET /users/1
// 2. 최대한 동사는 http method로 표현한다.
// ex) DELETE /users/1
// 단, HTTP METHOD로 표현할 수 없는 동사는 사용할 수 있다. (Controll URI)
// ex) 결제와 같이 직관적으로 표현할 수 없을 때는 사용한다.
// ex) POST /stock/1/pay

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
              Path saveDirectory = Paths.get(uploadPath, uuid);
              Path savePath = saveDirectory.resolve(originalFileName);

              boolean image = false;

              try
                {
                  if (!Files.exists(saveDirectory)) {
                    Files.createDirectories(saveDirectory);
                    }

                  multipartFile.transferTo(savePath);
                  if (Files.probeContentType(savePath).startsWith("image"))
                    {
                      image = true;
                      File thumbFile = new File(String.valueOf(saveDirectory), "s_" + originalFileName);
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
            log.info(list.toString());
            return list;
          }
        return null;
      }

    @GetMapping("/get/{fileid}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable("fileid") String fileid ,@PathVariable("filename") String filename)
      {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileid + File.separator + filename);
        HttpHeaders headers = new HttpHeaders();

        try
          {
          headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
            headers.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        }catch (Exception e)
          {
            return ResponseEntity.internalServerError().build();
          }
        return ResponseEntity.ok().headers(headers).body(resource);
      }

    @DeleteMapping("/delete/{fileid}/{filename}")
    public void deleteFile(@PathVariable("fileid") String fileid ,@PathVariable("filename") String filename)
      {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileid + File.separator + filename);

        try
          {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            Files.delete(resource.getFile().toPath());
            boardFileService.deleteBoardFile(fileid);
            if (contentType.startsWith("image"))
              {
                File thumbnail = new File(uploadPath + File.separator + fileid + File.separator +"s_"+ filename);
                Files.deleteIfExists(thumbnail.toPath());
              }
            Files.deleteIfExists(Paths.get(uploadPath + File.separator + fileid));
          }catch (Exception e)
          {
            log.error("Exception while deleting file [Err_Msg]: {}", e.getMessage());
          }
      }
  }
