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
