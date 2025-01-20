package com.thespace.thespace.controller;


import com.thespace.thespace.dto.board.BoardFileDTO;
import com.thespace.thespace.dto.board.UploadFilesDTO;
import com.thespace.thespace.service.BoardFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final BoardFileService boardFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<BoardFileDTO> upload(UploadFilesDTO uploadFilesDTO) {
        return boardFileService.upload(uploadFilesDTO);
    }

    @GetMapping("/{fileid}/{filename}")
    public ResponseEntity<Resource> get(@PathVariable("fileid") String fileid,
        @PathVariable("filename") String filename) {
        return boardFileService.get(fileid, filename);
    }

    @DeleteMapping("/{fileid}/{filename}")
    public void delete(@PathVariable("fileid") String fileid,
        @PathVariable("filename") String filename) {
        boardFileService.delete(fileid, filename);
    }
}
