package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.BoardFile;
import com.thespace.thespace.dto.board.BoardFileDTO;
import com.thespace.thespace.dto.board.UploadFilesDTO;
import com.thespace.thespace.repository.BoardFileRepository;
import com.thespace.thespace.repository.BoardRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardFileService {

    private final String uploadPath = "${com.thespace.upload.path}";
    private final BoardFileRepository boardFileRepository;
    private final BoardRepository boardRepository;

    public List<BoardFileDTO> upload(UploadFilesDTO uploadFilesDTO) {
        if (uploadFilesDTO.fileList() != null) {
            List<BoardFileDTO> list = new ArrayList<>();

            uploadFilesDTO.fileList().forEach(multipartFile -> {
                String originalFileName = "";
                if (multipartFile.getOriginalFilename() != null) {
                    originalFileName = multipartFile.getOriginalFilename();
                }
                String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                Path saveDirectory = Paths.get(uploadPath, uuid);
                Path savePath = saveDirectory.resolve(originalFileName);

                boolean image = false;

                try {
                    if (!Files.exists(saveDirectory)) {
                        Files.createDirectories(saveDirectory);
                    }

                    multipartFile.transferTo(savePath);

                    if (Files.probeContentType(savePath).startsWith("image")) {
                        image = true;
                        File thumbFile = new File(String.valueOf(saveDirectory),
                            "s_" + originalFileName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                } catch (IOException e) {
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

    public ResponseEntity<Resource> get(String fileid, String filename) {
        Resource resource = new FileSystemResource(
            uploadPath + File.separator + fileid + File.separator + filename);

        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
            headers.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void delete(String fileid, String filename) {

        Resource resource = new FileSystemResource(
            uploadPath + File.separator + fileid + File.separator + filename);

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            Files.delete(resource.getFile().toPath());

            Optional<BoardFile> boardFile = boardFileRepository.findById(fileid);
            if (boardFile.isPresent()) {
                Long bno = boardFile.get().getBoard().getBno();
                Optional<Board> board = boardRepository.findById(bno);

                if (board.isPresent()) {
                    Set<BoardFile> fileSet = board.get().getFileSet();
                    fileSet.remove(boardFile.get());
                    boardRepository.save(board.get());
                }

                boardFileRepository.deleteById(fileid);
            }

            if (contentType.startsWith("image")) {
                File thumbnail = new File(
                    uploadPath + File.separator + fileid + File.separator + "s_" + filename);
                Files.deleteIfExists(thumbnail.toPath());
            }
            Files.deleteIfExists(Paths.get(uploadPath + File.separator + fileid));
        } catch (Exception e) {
            log.error("Exception while deleting file [Err_Msg]: {}", e.getMessage());
        }
    }
}
